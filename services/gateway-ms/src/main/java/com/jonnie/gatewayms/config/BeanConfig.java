//package com.jonnie.gatewayms.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.springframework.http.HttpHeaders.*;
//
//@Configuration
//public class BeanConfig {
//    @Bean
//
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setAllowedHeaders(Arrays.asList(
//                ORIGIN,
//                CONTENT_TYPE,
//                ACCEPT,
//                AUTHORIZATION
//        ));
//        corsConfiguration.setAllowedMethods(Arrays.asList(
//                "GET",
//                "POST",
//                "PUT",
//                "DELETE",
//                "OPTIONS"));
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsFilter(source);
//    }
//
//
//}
