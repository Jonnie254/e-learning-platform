package com.jonnie.elearning.course.services;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.responses.CourseResponse;
import com.jonnie.elearning.course.responses.InstructorCourseResponse;
import com.jonnie.elearning.course.responses.SingleCourseResponse;
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
                .isPaid(false)
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

    public InstructorCourseResponse toInstructorCourseResponse(Course course) {
        return InstructorCourseResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .price(course.getPrice())
                .courseImageUrl(course.getCourseUrlImage())
                .category(course.getCategory().getCategoryName())
                .build();
    }

    public SingleCourseResponse toSingleCourseResponse(Course course) {
        return SingleCourseResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .courseImageUrl(course.getCourseUrlImage())
                .price(course.getPrice())
                .instructorName(course.getInstructorName())
                .whatYouWillLearn(course.getWhatYouWillLearn())
                .build();
    }
}
