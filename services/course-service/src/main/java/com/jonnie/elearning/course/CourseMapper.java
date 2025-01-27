package com.jonnie.elearning.course;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.user.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMapper {
    public Course toCourse(CourseRequest courseRequest, Category category, List<Tag> tags, UserResponse instructor) {
        return Course.builder()
                .courseId(courseRequest.courseId())
                .courseName(courseRequest.courseName())
                .description(courseRequest.courseDescription())
                .category(category)
                .tags(tags)
                .price(courseRequest.price())
                .instructorId(instructor.getId())
                .instructorName(instructor.getFullName())
                .isPublished(true)
                .whatYouWillLearn(courseRequest.whatYouWillLearn())
                .build();
    }
    public CourseResponse toCourseResponse(Course course) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseUrlImage(course.getCourseUrlImage())
                .InstructorName(course.getInstructorName())
                .price(course.getPrice())
                .whatYouWillLearn(course.getWhatYouWillLearn())
                .build();
    }
}
