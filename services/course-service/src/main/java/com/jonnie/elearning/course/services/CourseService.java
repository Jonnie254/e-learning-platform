package com.jonnie.elearning.course.services;

import com.cloudinary.Cloudinary;
import com.jonnie.elearning.course.responses.InstructorFullCourseDetailsResponse;
import com.jonnie.elearning.Repositories.CategoryRepository;
import com.jonnie.elearning.Repositories.CourseRepository;
import com.jonnie.elearning.Repositories.TagRepository;
import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.requests.UpdateCourseRequest;
import com.jonnie.elearning.course.responses.*;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.feign.enrollment.EnrollmentClient;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.feign.user.AuthenticationClient;
import com.jonnie.elearning.feign.user.UserResponse;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    public final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final CourseMapper courseMapper;
    private final AuthenticationClient authenticationClient;
    private final Cloudinary cloudinary;
    private final EnrollmentClient enrollmentClient;


    @Value("${application.cloudinary.folder}")
    private String folder;

    public String createCourse(CourseRequest courseRequest, String instructorId, MultipartFile courseImage) {
        //check whether the instructor exists(openfeign)
        UserResponse instructor = authenticationClient.findUserById(instructorId)
                .orElseThrow(() -> new BusinessException("Instructor cannot be found"));
        // check whether the category exists
        Category category = categoryRepository.findById(courseRequest.categoryId())
                .orElseThrow(() -> new BusinessException("Category cannot be found for ID: " + courseRequest.categoryId()));
        //check whether the tags exist
        if (courseRequest.tagIds() == null || courseRequest.tagIds().isEmpty()) {
            throw new BusinessException("Tag IDs cannot be empty");
        }
        //check whether the tags exist
        List<Tag> tags = courseRequest.tagIds().stream()
                .map(tagId -> {
                    Optional<Tag> tag = tagRepository.findById(tagId);
                    if (tag.isEmpty()) {
                        throw new BusinessException("Tag not found for ID: " + tagId);
                    }
                    return tag.get();
                })
                .toList();
        //save the Course banner to cloudinary
        String courseImageUrl = uploadCourseImage(courseImage);
        Course course = courseMapper.toCourse(courseRequest, category, tags, instructor);
        course.setCourseUrlImage(courseImageUrl);
        return courseRepository.save(course).getCourseId();
    }

    //upload course image to cloudinary
    private String uploadCourseImage(MultipartFile courseImage) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    courseImage.getBytes(),
                    Map.of(
                            "public_id", folder + "/" + courseImage.getOriginalFilename(),
                            "overwrite", true,
                            "resource_type", "auto"
                    )
            );
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new BusinessException("Error uploading course image: " + e.getMessage());
        }
    }

    //find all courses where the status is published -> for students
    public PageResponse<CourseResponse> findAllCoursesPublished(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseRepository.findAllPublishedCourses(pageable);
        List<CourseResponse> courseResponses = courses.stream()
                .map(courseMapper::toCourseResponse)
                .toList();
        return new PageResponse<>(
                courseResponses,
                courses.getNumber(),
                courses.getSize(),
                courses.getTotalElements(),
                courses.getTotalPages(),
                courses.isLast(),
                courses.isFirst()
        );
    }

    // Deactivate course
    public String deactivateCourse(String courseId, String instructorId, String userRole) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));

        // Instructor check
        if (ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            if (!course.getInstructorId().equals(instructorId)) {
                throw new BusinessException("You are not authorized to deactivate this course");
            }
        }

        // Admin check (no further checks needed)
        else if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            throw new BusinessException("You are not authorized to deactivate this course");
        }

        // Deactivate the course
        course.setPublished(false);
        courseRepository.save(course);

        return "Course deactivated successfully";
    }
    // Activate course
    public String activateCourse(String courseId, String instructorId, String userRole) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));

        // Instructor check
        if (ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            if (!course.getInstructorId().equals(instructorId)) {
                throw new BusinessException("You are not authorized to activate this course");
            }
        }
        // Admin check (no further checks needed)
        else if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            throw new BusinessException("You are not authorized to activate this course");
        }
        // Activate the course
        course.setPublished(true);
        courseRepository.save(course);

        return "Course activated successfully";
    }

    // Find all courses by instructor
    public PageResponse<InstructorCourseResponse> findCoursesByInstructor(String instructorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseRepository.findAllByInstructorId(instructorId, pageable);
        List<InstructorCourseResponse> instructorCourseResponses = courses.stream()
                .map(courseMapper::toInstructorCourseResponse)
                .toList();
        return new PageResponse<>(
                instructorCourseResponses,
                courses.getNumber(),
                courses.getSize(),
                courses.getTotalElements(),
                courses.getTotalPages(),
                courses.isLast(),
                courses.isFirst()
        );
    }

    public SingleCourseResponse findCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));
        return courseMapper.toSingleCourseResponse(course);
    }

    public CourseCartResponse findCourseByIdForCartItem(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));
        return courseMapper.toCourseCartResponse(course);
    }

    public PageResponse<CourseResponse> findCoursesAvailableForUser(String userId, int page, int size) {
        log.info("Getting enrolled courses for user: {}", userId);
        List<String> courseIds = enrollmentClient.getEnrolledCourses(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseRepository.findAllAvailableCourses(courseIds, pageable);
        List<CourseResponse> courseResponses = courses.stream()
                .map(courseMapper::toCourseResponse)
                .toList();
        return new PageResponse<>(
                courseResponses,
                courses.getNumber(),
                courses.getSize(),
                courses.getTotalElements(),
                courses.getTotalPages(),
                courses.isLast(),
                courses.isFirst()
        );
    }

    public List<CourseEnrollResponse> findCoursesByIds(List<String> courseIds) {
        List<Course> courses = courseRepository.findAllById(courseIds);
        return courseMapper.toCoursesResponse(courses);
    }
    public InstructorFullCourseDetailsResponse findCourseByIdForInstructor(String courseId, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new BusinessException("You are not authorized to view this course");
        }
        return courseMapper.toInstructorFullCourseResponse(course);
    }

    @Transactional
    public void updateCourse(@Valid UpdateCourseRequest updateCourseRequest,
                             String courseId,
                             String instructorId,
                             MultipartFile newCourseImage) {

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found for ID: " + courseId));

        if (!existingCourse.getInstructorId().equals(instructorId)) {
            throw new BusinessException("You are not authorized to update this course");
        }

        courseMapper.updateCourseFromRequest(updateCourseRequest, existingCourse);

        updateCategory(updateCourseRequest.categoryId(), existingCourse);
        updateTags(updateCourseRequest.tagIds(), existingCourse);

        if (newCourseImage != null && !newCourseImage.isEmpty()) {
            String newCourseImageUrl = uploadCourseImage(newCourseImage);
            existingCourse.setCourseUrlImage(newCourseImageUrl);
        }
        courseRepository.save(existingCourse);
    }

    private void updateCategory(String categoryId, Course existingCourse) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException("Category not found for ID: " + categoryId));
            existingCourse.setCategory(category);
        }
    }


    private void updateTags(List<String> tagIds, Course existingCourse) {
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> updatedTags = tagRepository.findAllById(tagIds);
            if (updatedTags.size() != tagIds.size()) {
                throw new BusinessException("One or more tags not found.");
            }
            existingCourse.setTags(updatedTags);
        }
    }

    public List<CourseDetailsResponse> getCoursesByIds(List<String> courseIds) {
        List<Course> courses = courseRepository.findAllById(courseIds);
        return courseMapper.toCourseDetailsResponse(courses);
    }
}
