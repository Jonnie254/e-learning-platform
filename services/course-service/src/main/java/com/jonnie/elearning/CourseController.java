package com.jonnie.elearning;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.requests.SectionRequest;
import com.jonnie.elearning.course.requests.UpdateSectionRequest;
import com.jonnie.elearning.course.responses.*;
import com.jonnie.elearning.course.services.SectionService;
import com.jonnie.elearning.course.services.CourseService;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;
    private final SectionService sectionService;

    //method to create a course
    @PostMapping(value = "/create-course", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> create(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @ModelAttribute @Valid CourseRequest courseRequest,
            @RequestParam MultipartFile courseImage
    ) {
        Map<String, String> response = new HashMap<>();
        if (userRole == null || instructorId == null) {
            response.put("message", "Missing required headers: X-User-Role or X-User-Id");
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            response.put("message", "You are not authorized to create a course");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (courseImage.isEmpty()) {
            response.put("message", "Course image is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            log.info("Creating course with name: {}", courseRequest);
            String courseId = courseService.createCourse(courseRequest, instructorId, courseImage);
            response.put("courseId", courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            response.put("message", "An error occurred while creating the course");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // method to find all courses
    @GetMapping("/all-courses")
    public ResponseEntity<PageResponse<CourseResponse>> findAllCourses(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(courseService.findAllCoursesPublished(page, size));
    }

    // method to toggle the course status
    @PutMapping("/{course-id}/toggle-status")
    public ResponseEntity<String> toggleCourseStatus(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("course-id") String courseId,
            @RequestParam(name = "action") String action
    ) {
        if (userRole == null || instructorId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing required headers: X-User-Role or X-User-Id");
        }

        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole) && !ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to change the course status");
        }

        if (courseId == null || courseId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course ID is required");
        }

        if (!"deactivate".equalsIgnoreCase(action) && !"activate".equalsIgnoreCase(action)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action. Allowed actions are 'deactivate' or 'activate'.");
        }

        String resultMessage = switch (action.toLowerCase()) {
            case "deactivate" -> courseService.deactivateCourse(courseId, instructorId, userRole);
            case "activate" -> courseService.activateCourse(courseId, instructorId, userRole);
            default -> throw new IllegalArgumentException("Invalid action");
        };

        return ResponseEntity.ok(resultMessage);
    }

    //get all the courses for the instructor using the instructor id
    @GetMapping("/instructor-courses")
    public ResponseEntity<PageResponse<InstructorCourseResponse>> findCoursesByInstructor(
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        if (instructorId == null || instructorId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(PageResponse.<InstructorCourseResponse>builder().build());
        }
        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(PageResponse.<InstructorCourseResponse>builder().build());
        }
        return ResponseEntity.ok(courseService.findCoursesByInstructor(instructorId, page, size));
    }

    //get the courses using the course id
    @GetMapping("/{course-id}")
    public ResponseEntity<SingleCourseResponse> findCourseById(
            @PathVariable("course-id") String courseId
    ) {
        return ResponseEntity.ok(courseService.findCourseById(courseId));
    }

    // get courses by id for the cart
    @GetMapping("/cart-item/{course-id}")
    public ResponseEntity<CourseCartResponse> findCourseByIdForCartItem(
            @PathVariable("course-id") String courseId
    ) {
        return ResponseEntity.ok(courseService.findCourseByIdForCartItem(courseId));
    }

    // create section for the course
    @PostMapping(value="/{courseId}/create-content", consumes = "multipart/form-data")
    public ResponseEntity<String> createContent(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("courseId") String courseId,
            @ModelAttribute @Valid SectionRequest sectionRequest,
            @RequestParam MultipartFile sectionPdf,
            @RequestParam MultipartFile sectionVideo
    ){
        if(userRole == null || instructorId == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required headers: X-User-Role or X-User-Id");
        }
        if(!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create a course");
        }
        if (sectionPdf.isEmpty() || sectionVideo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Section PDF and Video are required");
        }
        try {
            String sectionId = sectionService.createSection(sectionRequest, courseId, instructorId, sectionPdf, sectionVideo);
            return ResponseEntity.status(HttpStatus.CREATED).body(sectionId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the course content");

        }
    }

    //get section by id
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<SectionResponse> findSectionById(
            @PathVariable("sectionId") String sectionId
    ) {
        return ResponseEntity.ok(sectionService.findSectionById(sectionId));
    }

    //get all sections for a course
    @GetMapping("/{courseId}/sections")
    public ResponseEntity<PageResponse<SectionResponse>> findSectionsByCourse(
            @PathVariable("courseId") String courseId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sectionService.findSectionsByCourse(courseId, page, size));
    }
    //get the section ids for a course
    @GetMapping("/sections/{course-id}")
    public ResponseEntity<AllSectionId> findSectionIdsByCourse(
            @PathVariable("course-id") String courseId
    ) {
        return ResponseEntity.ok(sectionService.findSectionIdsByCourse(courseId));
    }

    //update the section content
    @PutMapping("/section/{section-id}/update-content")
    public ResponseEntity<String> updateSectionContent(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("section-id") String sectionId,
            @ModelAttribute  UpdateSectionRequest updateSectionRequest,
            @RequestParam(required = false) MultipartFile newSectionPdf,
            @RequestParam(required = false) MultipartFile newSectionVideo
    ) {
        if (userRole == null || instructorId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required headers: X-User-Role or X-User-Id");
        }
        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update a section");
        }
        try {
            sectionService.updateSectionContent(updateSectionRequest, sectionId, instructorId, newSectionPdf, newSectionVideo);
            return ResponseEntity.status(HttpStatus.OK).body("Section content updated successfully");
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the section content");
        }
    }

    //get the courses but exclude the ones that the user is enrolled in
    @GetMapping("/available-for-user")
    public ResponseEntity<PageResponse<CourseResponse>> findCoursesAvailableForUser(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(courseService.findCoursesAvailableForUser(userId, page, size));
    }

    //get the courses by ids
    @PostMapping("/courses-by-ids")
    public ResponseEntity<List<CourseEnrollResponse>> findCoursesByIds(
            @RequestBody List<String> courseIds
    ) {
        return ResponseEntity.ok(courseService.findCoursesByIds(courseIds));
    }
}