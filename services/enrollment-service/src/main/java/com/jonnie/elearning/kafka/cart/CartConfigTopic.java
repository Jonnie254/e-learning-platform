package com.jonnie.elearning.kafka.cart;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CartConfigTopic {
    @Bean
    public NewTopic cartTopic(){
        return TopicBuilder
                .name("cart-topic")
                .build();
    }
}
