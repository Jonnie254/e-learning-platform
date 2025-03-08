package com.jonnie.elearning.course.services;

import com.cloudinary.Cloudinary;
import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.requests.CourseRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(cloudinary.uploader().upload(any(), any()))
                .thenReturn(Map.of("url", "https://example.com/course.jpg"));

        when(courseMapper.toCourse(any(), any(), any(), any()))
                .thenReturn(course);

        when(courseRepository.save(any(Course.class)))
                .thenReturn(course);
        String courseId = courseService.createCourse(courseRequest, "inst123", mockImage);
        assertThat(courseId).isEqualTo("course123");
        verify(courseRepository).save(any(Course.class));
    }
}