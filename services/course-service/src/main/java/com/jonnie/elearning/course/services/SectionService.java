package com.jonnie.elearning.course.services;

import com.jonnie.elearning.repositories.CourseRepository;
import com.jonnie.elearning.repositories.SectionRepository;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.Section;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.requests.SectionRequest;
import com.jonnie.elearning.course.requests.UpdateSectionRequest;
import com.jonnie.elearning.course.responses.AllSectionId;
import com.jonnie.elearning.course.responses.InstructorSectionResponse;
import com.jonnie.elearning.course.responses.SectionResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.feign.enrollment.EnrollmentClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final S3Client amazonS3;
    private final EnrollmentClient enrollmentClient;
    private final CourseMapper courseMapper;
    @Value("${application.aws.bucket-name}")
    private String awsBucketName;
    @Value("${application.aws.region}")
    private String awsRegion;

    //create a section
    public String createSection(
            @Valid SectionRequest sectionRequest,
            String courseId,
            String instructorId,
            MultipartFile sectionPdf,
            MultipartFile sectionVideo) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));
        if (!instructorId.equals(course.getInstructorId())) {
            throw new BusinessException("You can't create content for a course you don't own");
        }
        String sectionPdfUrl = uploadFileToS3(sectionPdf, "pdf");
        String sectionVideoUrl = uploadFileToS3(sectionVideo, "video");
        Section section = courseMapper.toSection(sectionRequest, sectionPdfUrl, sectionVideoUrl, course);
        sectionRepository.save(section);
        return section.getSectionId();
    }

    //upload file to s3
    private String uploadFileToS3(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(fileType + " file is required");
        }
        try {
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = fileType + "_" + System.currentTimeMillis() + "_" + originalFileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsBucketName)
                    .key(uniqueFileName)
                    .build();
            amazonS3.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return "https://" + awsBucketName + ".s3." + awsRegion + ".amazonaws.com/" + uniqueFileName;
        } catch (Exception e) {
            throw new BusinessException("Error uploading file to S3: " + e.getMessage());
        }
    }


    //find all sections for a course
    public PageResponse<SectionResponse> findSectionsByCourse(String courseId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));
        enrollmentClient.initializeProgress(courseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Section> sections = sectionRepository.findByCourse(course, pageable);
        List<SectionResponse> sectionResponses = sections.stream()
                .map(courseMapper::fromSection)
                .toList();
        return new PageResponse<>(
                sectionResponses,
                sections.getNumber(),
                sections.getSize(),
                sections.getTotalElements(),
                sections.getTotalPages(),
                sections.isLast(),
                sections.isFirst()
        );
    }

    public SectionResponse findSectionById(String sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new BusinessException("Section not found"));
        return courseMapper.fromSection(section);
    }

    public void updateSectionContent(@Valid UpdateSectionRequest updateSectionRequest,
                                     String sectionId,
                                     String instructorId,
                                     MultipartFile newSectionPdf,
                                     MultipartFile newSectionVideo) {
        // Check if section exists
        Section existingSection = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new BusinessException("Section not found"));

        // Check if instructor owns the course
        if (!instructorId.equals(existingSection.getCourse().getInstructorId())) {
            throw new BusinessException("You can't update content for a course you don't own");
        }
        String updatedSectionPdfUrl = existingSection.getPdfUrl();
        String updatedSectionVideoUrl = existingSection.getVideoUrl();
        if (newSectionPdf != null && !newSectionPdf.isEmpty()) {
            deleteFileFromS3(updatedSectionPdfUrl);
            updatedSectionPdfUrl = uploadFileToS3(newSectionPdf, "pdf");
        }
        if (newSectionVideo != null && !newSectionVideo.isEmpty()) {
            deleteFileFromS3(updatedSectionVideoUrl);
            updatedSectionVideoUrl = uploadFileToS3(newSectionVideo, "video");
        }
        Section updatedSection = courseMapper.toUpdateSection(updateSectionRequest,
                updatedSectionPdfUrl,
                updatedSectionVideoUrl,
                existingSection);
        sectionRepository.save(updatedSection);
    }


    //delete file from s3
    private void deleteFileFromS3(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            try {
                String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                amazonS3.deleteObject(builder -> builder.bucket(awsBucketName).key(fileKey));
            } catch (Exception e) {
                throw new BusinessException("Error deleting file from S3: " + e.getMessage());
            }
        }
    }

    //get all section ids for a course
    public AllSectionId findSectionIdsByCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));

        List<String> sectionIds = sectionRepository.findSectionIdsByCourse(course);

        if (sectionIds.isEmpty()) {
            log.warn("No sections found for course: {}", courseId);
        } else {
            log.info("Found {} sections for course: {}", sectionIds.size(), courseId);
        }

        return new AllSectionId(sectionIds);
    }

    public PageResponse<InstructorSectionResponse> findSectionsByCourseForInstructor(
            String courseId, String instructorId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));
        if (!instructorId.equals(course.getInstructorId())) {
            throw new BusinessException("You can't view content for a course you don't own");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Section> sections = sectionRepository.findByCourse(course, pageable);
        List<InstructorSectionResponse> sectionResponses = sections.stream()
                .map(courseMapper::fromInstructorSection)
                .toList();
        return new PageResponse<>(
                sectionResponses,
                sections.getNumber(),
                sections.getSize(),
                sections.getTotalElements(),
                sections.getTotalPages(),
                sections.isLast(),
                sections.isFirst()
        );
    }
}
