package com.jonnie.elearning;

import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.payment.PaymentRequest;
import com.jonnie.elearning.payment.PaymentService;
import com.jonnie.elearning.payment.responses.InstructorEarningResponse;
import com.jonnie.elearning.utils.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;


    private void validateUser(String userId, String userRole, ROLE requiredRole) {
        if (userId == null || userId.isEmpty() || userRole == null || userRole.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User ID and role are required");
        }
        if (!requiredRole.name().equalsIgnoreCase(userRole)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to perform this operation");
        }
    }

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(
            @RequestBody PaymentRequest paymentRequest
    ){
        try {
            String approvalUrl = paymentService.createPayPalPayment(paymentRequest);
            return ResponseEntity.ok(Map.of("approvalUrl", approvalUrl));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment processing failed"));
        }
    }
    @GetMapping("/success")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("token") String token,
            @RequestParam("PayerID") String payerId
    ) {
        try {
            boolean isPaymentSuccess = paymentService.completePayPalPayment(paymentId, token, payerId);
            if (isPaymentSuccess) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "http://localhost:4200/my-courses")
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Payment verification failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the payment");
        }
    }

    // get the total earnings of an instructor
    @GetMapping("/instructors-total-earnings")
    public ResponseEntity<PageResponse<InstructorEarningResponse>> getInstructorTotalEarnings(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestParam(name = "page",required = false, defaultValue = "0") int page,
            @RequestParam(name="size",required = false, defaultValue = "10") int size
    ) {
        validateUser(userId, userRole, ROLE.INSTRUCTOR);
        try {
            PageResponse<InstructorEarningResponse> response = paymentService.getInstructorTotalEarnings(userId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PageResponse<>(List.of(), page, size, 0, 0, true, true));
        }
    }
}
