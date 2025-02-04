package com.jonnie.elearning.course.services;

import com.jonnie.elearning.category.Category;
import com.jonnie.elearning.course.Course;
import com.jonnie.elearning.course.Section;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.requests.SectionRequest;
import com.jonnie.elearning.course.requests.UpdateSectionRequest;
import com.jonnie.elearning.course.responses.*;
import com.jonnie.elearning.tag.Tag;
import com.jonnie.elearning.user.UserResponse;
import jakarta.validation.Valid;
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

    // Map Section
    public Section toSection(@Valid SectionRequest sectionRequest, String sectionPdfUrl, String sectionVideoUrl, Course course) {
        return Section.builder()
                .sectionId(sectionRequest.sectionId())
                .sectionName(sectionRequest.sectionName())
                .course(course)
                .sectionDescription(sectionRequest.sectionDescription())
                .pdfUrl(sectionPdfUrl)
                .videoUrl(sectionVideoUrl)
                .build();
    }

    public SectionResponse fromSection(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .sectionName(section.getSectionName())
                .sectionDescription(section.getSectionDescription())
                .pdfUrl(section.getPdfUrl())
                .videoUrl(section.getVideoUrl())
                .courseId(section.getCourse().getCourseId())
                .build();
    }

    public Section toUpdateSection(@Valid
                                UpdateSectionRequest updateSectionRequest,
                                String updatedsectionPdfUrl,
                                String updatedsectionVideoUrl,
                                   Section existingSection) {
        if (updateSectionRequest.sectionName() != null) {
            existingSection.setSectionName(updateSectionRequest.sectionName());
        }
        if (updateSectionRequest.sectionDescription() != null) {
            existingSection.setSectionDescription(updateSectionRequest.sectionDescription());
        }
        if (updatedsectionPdfUrl != null) {
            existingSection.setPdfUrl(updatedsectionPdfUrl);
        }
        if (updatedsectionVideoUrl != null) {
            existingSection.setVideoUrl(updatedsectionVideoUrl);
        }
        return existingSection;
    }

    public CourseCartResponse toCourseCartResponse(Course course) {
        return CourseCartResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .price(course.getPrice())
                .instructorId(course.getInstructorId())
                .instructorName(course.getInstructorName())
                .courseImageUrl(course.getCourseUrlImage())
                .build();
    }

    public AllSectionId fromSections(List<Section> sections) {
        return AllSectionId.builder()
                .sectionIds(sections
                        .stream().
                        map(Section::getSectionId)
                        .toList())
                .build();
    }
}


