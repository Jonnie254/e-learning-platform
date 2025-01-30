package com.jonnie.elearning.openfeign.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "payment-service",
        url = "${application.config.payment-url}"
)
public interface PaymentClient {
    @PostMapping("/create-payment")
    ResponseEntity<Map<String, String>> requestToMakePayment(@RequestBody PaymentRequest paymentRequest);
}
