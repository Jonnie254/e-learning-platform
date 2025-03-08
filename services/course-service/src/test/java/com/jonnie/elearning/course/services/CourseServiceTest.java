package com.jonnie.elearning.course.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.responses.CourseResponse;
import com.jonnie.elearning.course.responses.InstructorCourseResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.feign.enrollment.EnrollmentClient;
import com.jonnie.elearning.feign.user.AuthenticationClient;
import com.jonnie.elearning.feign.user.UserResponse;
import com.jonnie.elearning.repositories.CategoryRepository;
import com.jonnie.elearning.repositories.CourseRepository;
import com.jonnie.elearning.repositories.TagRepository;
import com.jonnie.elearning.tag.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class  CourseServiceTest {
    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private AuthenticationClient authenticationClient;
    @Mock
    private Cloudinary cloudinary;
    @Mock
    private EnrollmentClient enrollmentClient;

    private MultipartFile mockImage;
    private CourseRequest courseRequest;
    private Category category;
    private List<Tag> tags;
    private Course course;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId("cat123");
        category.setCategoryName("Programming");

        Tag tag1 = new Tag();
        tag1.setTagId("tag1");
        tag1.setTagName("Java");

        Tag tag2 = new Tag();
        tag2.setTagId("tag2");
        tag2.setTagName("Spring Boot");

        tags = List.of(tag1, tag2);

        courseRequest = new CourseRequest(
                null,
                "Java Basics",
                "Learn Java from scratch",
                "cat123",
                BigDecimal.valueOf(49.99),
                List.of("tag1", "tag2"),
                List.of("OOP Basics", "Exception Handling", "Collections")
        );

        // Mock course
        course = new Course();
        course.setCourseId("course123");
        course.setCourseName("Java Basics");
        course.setCategory(category);
        course.setTags(tags);
        course.setInstructorId("inst123");

        mockImage = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-data".getBytes());
    }
    @Test
    void testCreateCourse_Success() throws IOException {
        when(authenticationClient.findUserById("inst123"))
                .thenReturn(Optional.of(new UserResponse("inst123", "John", "Doe", "john@example.com", "profile-pic.jpg")));
        when(categoryRepository.findById("cat123"))
                .thenReturn(Optional.of(category));

        when(tagRepository.findById("tag1")).thenReturn(Optional.of(tags.get(0)));
        when(tagRepository.findById("tag2")).thenReturn(Optional.of(tags.get(1)));

        Uploader uploaderMock = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploaderMock);
        when(uploaderMock.upload(any(), any()))
                .thenReturn(Map.of("url", "https://example.com/course.jpg"));

        when(courseMapper.toCourse(any(), any(), any(), any()))
                .thenReturn(course);

        when(courseRepository.save(any(Course.class)))
                .thenReturn(course);
        String courseId = courseService.createCourse(courseRequest, "inst123", mockImage);
        assertThat(courseId).isEqualTo("course123");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testCreateCourse_InstructorNotFound() {
        when(authenticationClient.findUserById("inst123"))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                courseService.createCourse(courseRequest, "inst123", mockImage));

        assertThat(exception.getMessage()).isEqualTo("Instructor cannot be found");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testCreateCourse_CategoryNotFound() {
        when(authenticationClient.findUserById("inst123"))
                .thenReturn(Optional.of(new UserResponse("inst123", "John", "Doe", "john@example.com", "profile-pic.jpg")));
        when(categoryRepository.findById("cat123"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () ->
                courseService.createCourse(courseRequest, "inst123", mockImage));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testCreateCourse_Fails_WhenImageUploadsFails() throws IOException {
        when(authenticationClient.findUserById("inst123"))
                .thenReturn(Optional.of(new UserResponse("inst123", "John", "Doe", "john@example.com", "profile-pic.jpg")));

        when(categoryRepository.findById("cat123"))
                .thenReturn(Optional.of(category));
        when(tagRepository.findById("tag1")).thenReturn(Optional.of(tags.get(0)));
        when(tagRepository.findById("tag2")).thenReturn(Optional.of(tags.get(1)));

        when(cloudinary.uploader()).thenReturn(mock(Uploader.class));
        when(cloudinary.uploader().upload(any(), any()))
                .thenThrow(new IOException("Failed to upload image"));
        assertThrows(BusinessException.class, () ->
                courseService.createCourse(courseRequest, "inst123", mockImage));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testFindAllCoursesPublished() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Course course1 = new Course();
        course1.setCourseId("course1");
        course1.setCourseName("Java Basics");

        Course course2 = new Course();
        course2.setCourseId("course2");
        course2.setCourseName("Spring Boot");

        List<Course> courseList = List.of(course1, course2);
        Page<Course> coursePage = new PageImpl<>(courseList, pageable, courseList.size());

        when(courseRepository.findAllPublishedCourses(any(Pageable.class)))
                .thenReturn(coursePage);

        CourseResponse courseResponse1 = new CourseResponse();
        courseResponse1.setCourseId("course1");
        courseResponse1.setCourseName("Java Basics");

        CourseResponse courseResponse2 = new CourseResponse();
        courseResponse2.setCourseId("course2");
        courseResponse2.setCourseName("Spring Boot");

        when(courseMapper.toCourseResponse(course1)).thenReturn(courseResponse1);
        when(courseMapper.toCourseResponse(course2)).thenReturn(courseResponse2);

        // When
        PageResponse<CourseResponse> result = courseService.findAllCoursesPublished(page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getCourseId()).isEqualTo("course1");
        assertThat(result.getContent().get(1).getCourseId()).isEqualTo("course2");
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        verify(courseRepository).findAllPublishedCourses(any(Pageable.class));
    }

    @Test
    void testDeactivateCourse_AsInstructor_Success() {
        // Given
        String courseId = "course123";
        String instructorId = "inst123";
        String userRole = "INSTRUCTOR";

        Course course = new Course();
        course.setCourseId(courseId);
        course.setInstructorId(instructorId);
        course.setPublished(true);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When
        String result = courseService.deactivateCourse(courseId, instructorId, userRole);

        // Then
        assertThat(result).isEqualTo("Course deactivated successfully");
        assertThat(course.isPublished()).isFalse();
        verify(courseRepository).save(course);
    }

    @Test
    void testDeactivateCourse_AsInstructor_NotAuthorized() {
        // Given
        String courseId = "course123";
        String instructorId = "inst456";
        String userRole = "INSTRUCTOR";

        Course course = new Course();
        course.setCourseId(courseId);
        course.setInstructorId("inst123");
        course.setPublished(true);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                courseService.deactivateCourse(courseId, instructorId, userRole));

        assertThat(exception.getMessage()).isEqualTo("You are not authorized to deactivate this course");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testDeactivateCourse_CourseNotFound() {
        // Given
        String courseId = "course123";
        String instructorId = "inst123";
        String userRole = "INSTRUCTOR";

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                courseService.deactivateCourse(courseId, instructorId, userRole));

        assertThat(exception.getMessage()).isEqualTo("Course not found for ID: " + courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testFindCoursesBy_Instructor() {
        // Given
        int page = 0;
        int size = 10;
        String instructorId = "inst123";
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Create sample Course objects for the instructor
        Course course1 = new Course();
        course1.setCourseId("course1");
        course1.setCourseName("Course One");
        course1.setInstructorId(instructorId);

        Course course2 = new Course();
        course2.setCourseId("course2");
        course2.setCourseName("Course Two");
        course2.setInstructorId(instructorId);

        List<Course> courseList = List.of(course1, course2);
        Page<Course> coursePage = new PageImpl<>(courseList, pageable, courseList.size());

        // Stub the repository to return the sample Page of courses
        when(courseRepository.findAllByInstructorId(eq(instructorId), any(Pageable.class)))
                .thenReturn(coursePage);

        // Stub the mapper to convert Course objects to InstructorCourseResponse
        InstructorCourseResponse response1 = new InstructorCourseResponse();
        response1.setCourseId("course1");
        response1.setCourseName("Course One");

        InstructorCourseResponse response2 = new InstructorCourseResponse();
        response2.setCourseId("course2");
        response2.setCourseName("Course Two");

        when(courseMapper.toInstructorCourseResponse(course1)).thenReturn(response1);
        when(courseMapper.toInstructorCourseResponse(course2)).thenReturn(response2);

        // When
        PageResponse<InstructorCourseResponse> result = courseService.findCoursesByInstructor(instructorId, page, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent());
        assertThat(result.getContent().get(0).getCourseId()).isEqualTo("course1");
        assertThat(result.getContent().get(1).getCourseId()).isEqualTo("course2");
        assertThat(result.getTotalElements()).isEqualTo(coursePage.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(coursePage.getTotalPages());
        assertThat(result.isLast()).isEqualTo(coursePage.isLast());
        assertThat(result.isFirst()).isEqualTo(coursePage.isFirst());
        verify(courseRepository).findAllByInstructorId(eq(instructorId), any(Pageable.class));
    }



}