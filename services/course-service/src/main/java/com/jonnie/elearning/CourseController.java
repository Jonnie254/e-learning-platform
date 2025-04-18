package com.jonnie.elearning;


import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.course.requests.CourseRequest;
import com.jonnie.elearning.course.requests.SectionRequest;
import com.jonnie.elearning.course.requests.UpdateCourseRequest;
import com.jonnie.elearning.course.requests.UpdateSectionRequest;
import com.jonnie.elearning.course.responses.*;
import com.jonnie.elearning.course.services.SectionService;
import com.jonnie.elearning.course.services.CourseService;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.recommendation.RecommendationService;
import com.jonnie.elearning.utils.ROLE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;
    private final SectionService sectionService;
    private final RecommendationService recommendationService;

    private ResponseEntity<Map<String, String>> validateInstructorHeaders(String userRole, String instructorId) {
        if (userRole == null || instructorId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing required headers: X-User-Role or X-User-Id"));
        }
        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You are not authorized to perform this action"));
        }
        return null;
    }
    private void validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User ID and role are required");
        }
        if (!requiredRole.name().equalsIgnoreCase(userRole)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to perform this operation");
        }
    }

    //method to create a course
    @PostMapping(value = "/create-course", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> create(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @ModelAttribute @Valid CourseRequest courseRequest,
            @RequestParam MultipartFile courseImage
    ) {
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            return validationResponse;
        }
        if (courseImage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Course image is required"));
        }
        try {
            String courseId = courseService.createCourse(courseRequest, instructorId, courseImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("courseId", courseId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while creating the course"));
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
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Objects.requireNonNull(validationResponse.getBody()).get("message"));
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
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            return ResponseEntity.status(validationResponse.getStatusCode()).body(PageResponse.<InstructorCourseResponse>builder().build());
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

    // get the instructor id using the course id
    @PostMapping("/instructor/{courseId}")
    public ResponseEntity<String> findInstructorIdByCourseId(
            @PathVariable("courseId") String courseId
    ) {
        log.info("Finding instructor ID for course with ID: {}", courseId);
        return ResponseEntity.ok(courseService.findInstructorIdByCourseId(courseId));
    }

    //update the course details using the course id
    @PutMapping("/update-course/{courseId}")
    public ResponseEntity<Map<String, String>> updateCourse(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("courseId") String courseId,
            @ModelAttribute @Valid UpdateCourseRequest updateCourseRequest,
            @RequestParam(required = false) MultipartFile newCourseImage
    ) {
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {
            courseService.updateCourse(updateCourseRequest, courseId, instructorId, newCourseImage);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Course updated successfully"));
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while updating the course"));
        }
    }

    // get courses by id for the cart
    @GetMapping("/cart-item/{course-id}")
    public ResponseEntity<CourseCartResponse> findCourseByIdForCartItem(
            @PathVariable("course-id") String courseId
    ) {
        return ResponseEntity.ok(courseService.findCourseByIdForCartItem(courseId));
    }

    // create section for the course
    @PostMapping(value = "/create-section-content/{courseId}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createContent(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("courseId") String courseId,
            @ModelAttribute @Valid SectionRequest sectionRequest,
            @RequestParam MultipartFile sectionPdf,
            @RequestParam MultipartFile sectionVideo
    ) {
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Missing required headers: X-User-Role or X-User-Id");
            return ResponseEntity.status(validationResponse.getStatusCode()).body(errorResponse);
        }
        if (sectionPdf.isEmpty() || sectionVideo.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Section PDF and Video are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        try {
            String sectionId = sectionService.createSection(sectionRequest, courseId, instructorId, sectionPdf, sectionVideo);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("sectionId", sectionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while creating the course content");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //get the course all the details for the instructor
    @GetMapping("/instructor-course/{courseId}")
    public ResponseEntity<?> findCourseByIdForInstructor(
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @PathVariable("courseId") String courseId
    ) {
        if (instructorId == null || instructorId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required headers: X-User-Id"));
        }

        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
        }

        InstructorFullCourseDetailsResponse courseDetails = courseService.findCourseByIdForInstructor(courseId, instructorId);
        return ResponseEntity.ok(courseDetails);
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

    //get the section ids for a course for the user
    @GetMapping("/sections/{course-id}")
    public ResponseEntity<AllSectionId> findSectionIdsByCourse(
            @PathVariable("course-id") String courseId
    ) {
        return ResponseEntity.ok(sectionService.findSectionIdsByCourse(courseId));
    }

    //get the sections for course and to a specific instructor
    @GetMapping("/instructor-course-sections/{courseId}")
    public ResponseEntity<PageResponse<InstructorSectionResponse>> findSectionsByCourseForInstructor(
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @PathVariable("courseId") String courseId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        if (instructorId == null || instructorId.isEmpty()) {
            return ResponseEntity.badRequest().body(PageResponse.<InstructorSectionResponse>builder().build());
        }

        if (!ROLE.INSTRUCTOR.name().equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(PageResponse.<InstructorSectionResponse>builder().build());
        }

        return ResponseEntity.ok(sectionService.findSectionsByCourseForInstructor(courseId, instructorId, page, size));
    }

    //update the section content
    @PutMapping("/section/update-content/{sectionId}")
    public ResponseEntity<Map<String, String>> updateSectionContent(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @PathVariable("sectionId") String sectionId,
            @ModelAttribute UpdateSectionRequest updateSectionRequest,
            @RequestParam(required = false) MultipartFile newSectionPdf,
            @RequestParam(required = false) MultipartFile newSectionVideo
    ) {
        ResponseEntity<Map<String, String>> validationResponse = validateInstructorHeaders(userRole, instructorId);
        if (validationResponse != null) {
            return validationResponse;
        }
        Map<String, String> response = new HashMap<>();
        try {
            sectionService.updateSectionContent(updateSectionRequest, sectionId, instructorId, newSectionPdf, newSectionVideo);
            response.put("message", "Section content updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BusinessException ex) {
            response.put("message", "An error occurred while updating the section content");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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

    //get the course chat room info
    @PostMapping("/course-chat-info")
    public ResponseEntity<List<CourseChatResponse>> getCourseChatInfo(
            @RequestBody List<String> courseIds) {
        List<CourseChatResponse> courseResponses = courseService.getCoursesInfo(courseIds);
        return ResponseEntity.ok(courseResponses);
    }

    //get the courses earnings
    @PostMapping("/courses-earnings")
    public ResponseEntity<List<CourseDetailsResponse>> getCoursesByIds(
            @RequestBody List<String> courseIds
    ) {
        return ResponseEntity.ok(courseService.getCoursesByIds(courseIds));
    }

    @PostMapping("/course-details-revenue")
    public ResponseEntity<CourseDetailsResponse> getCourseDetails(
            @RequestBody String key
    ) {
        return ResponseEntity.ok(courseService.getCourseDetails(key));
    }

    //get the instructors total courses
    @GetMapping("/instructor-total-courses")
    public ResponseEntity<TotalCoursesResponse> getInstructorTotalCourses(
            @RequestHeader(value = "X-User-Id", required = false) String instructorId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        validateUser(instructorId, userRole, ROLE.INSTRUCTOR);
        return ResponseEntity.ok(courseService.getInstructorTotalCourses(instructorId));
    }

    //get the total courses for the admin
    @GetMapping("/admin-total-courses")
    public ResponseEntity<TotalCoursesResponse> getAdminTotalCourses(
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        if (userRole == null || userRole.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User role is required");
        }
        if (!ROLE.ADMIN.name().equalsIgnoreCase(userRole)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to perform this operation");
        }
        return ResponseEntity.ok(courseService.getAdminTotalCourses());
    }

    //get the recommendations for the user
    @GetMapping("/recommend-user-courses")
    public ResponseEntity<PageResponse<CourseResponse>> getRecommendations(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ){
        return ResponseEntity.ok(recommendationService.getRecommendations(userId, page, size));
    }

}