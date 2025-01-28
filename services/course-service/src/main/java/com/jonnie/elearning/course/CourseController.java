package com.jonnie.elearning.course;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.responses.CourseResponse;
import com.jonnie.elearning.course.responses.InstructorCourseResponse;
import com.jonnie.elearning.course.responses.SingleCourseResponse;
import com.jonnie.elearning.course.services.CourseService;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;
    @PostMapping(value = "/create-course", consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @ModelAttribute @Valid CourseRequest courseRequest,
            @RequestParam MultipartFile courseImage
    ) {
        if (userRole == null || instructorId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required headers: X-User-Role or X-User-Id");
        }
        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create a course");
        }
        if (courseImage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course image is required");
        }
        try {
            String courseId = courseService.createCourse(courseRequest, instructorId, courseImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(courseId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the course");
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

    // create content for the course
    @PostMapping("/{course-id}/create-content")
    public ResponseEntity<String> createContent(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("course-id") String courseId,
            @ModelAttribute @Valid CourseRequest courseRequest

    ){
        if(userRole == null || instructorId == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required headers: X-User-Role or X-User-Id");
        }
        if(!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create a course");
        }


    }
}