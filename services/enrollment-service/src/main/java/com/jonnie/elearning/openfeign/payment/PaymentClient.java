package com.jonnie.elearning.openfeign.payment;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "payment-service",
        url = "${application.config.payment-url}"
)
public interface PaymentClient {
}
