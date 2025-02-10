package com.jonnie.elearning;

import com.jonnie.elearning.payment.PaymentRequest;
import com.jonnie.elearning.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

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
                        .header(HttpHeaders.LOCATION, "http://localhost:4200/payment-success")
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

}
