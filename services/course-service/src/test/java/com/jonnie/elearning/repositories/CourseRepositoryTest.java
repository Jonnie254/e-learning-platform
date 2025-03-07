package com.jonnie.elearning.repositories;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.course.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    Course course;
    Tag tag;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .categoryName("AI")
                .build();
        category = categoryRepository.save(category);

        Tag tag1 = Tag.builder().tagName("Java").build();
        Tag tag2 = Tag.builder().tagName("Spring Boot").build();

        List<Tag> tags = new ArrayList<>();
        tags.add(tagRepository.save(tag1));
        tags.add(tagRepository.save(tag2));

        course = Course.builder()
                .courseName("Java")
                .courseUrlImage("https://www.google.com")
                .instructorId("1")
                .instructorName("Jonnie")
                .price(BigDecimal.valueOf(100))
                .isPublished(true)
                .isPaid(true)
                .whatYouWillLearn(List.of("Java", "Spring Boot"))
                .description("Java course")
                .sections(List.of())
                .tags(tags)
                .category(category)
                .build();

        courseRepository.save(course);
    }


    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
    }

    @Test
    void testFindAllPublishedCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> publishedCourses = courseRepository.findAllPublishedCourses(pageable);
        assertThat(publishedCourses).isNotEmpty();
        assertThat(publishedCourses.getContent()).allMatch(Course::isPublished);
        assertThat(publishedCourses.getContent()).anyMatch(course -> course.getCourseName().equals("Java"));
        assertThat(publishedCourses.getContent()).anyMatch(course -> !course.getTags().isEmpty());
    }

    @Test
    void testFindAllByInstructorId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> courses = courseRepository.findAllByInstructorId("1", pageable);
        assertThat(courses).isNotEmpty();
        assertThat(courses.getContent()).allMatch(course -> course.getInstructorId().equals("1"));
    }

    @Test
    void testFindAllAvailableCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> availableCourses = courseRepository.findAllAvailableCourses(List.of(course.getCourseId()), pageable);
        assertThat(availableCourses).isEmpty();
    }

    @Test
    void testCountByInstructorId() {
        Long count = courseRepository.countByInstructorId("1");
        assertThat(count).isEqualTo(1);
    }

}
