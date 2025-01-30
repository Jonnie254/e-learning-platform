package com.jonnie.elearning.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    @Value("${application.paypal.client-Id}")
    private String clientId;
    @Value("${application.paypal.client-Secret}")
    private String clientSecret;
    @Value("${application.paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
