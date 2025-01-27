package com.jonnie.elearning.course;

import com.cloudinary.Cloudinary;
import com.jonnie.elearning.Repositories.CategoryRepository;
import com.jonnie.elearning.Repositories.CourseRepository;
import com.jonnie.elearning.Repositories.TagRepository;
import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.config.CloudinaryConfig;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.user.AuthenticationClient;
import com.jonnie.elearning.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
}
