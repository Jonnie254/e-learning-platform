package com.jonnie.elearning.course;


import com.jonnie.elearning.common.PageResponse;
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


}