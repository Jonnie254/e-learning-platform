package com.jonnie.elearning.config.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Bean
    public NewTopic enrollment(){
        return TopicBuilder
                .name("enrollment-topic")
                .build();
    }

    @Bean
    public NewTopic payment(){
        return TopicBuilder
                .name("payment-topic")
                .build();
    }

    @Bean
    public NewTopic cart(){
        return TopicBuilder
                .name("cart-status-topic")
                .build();
    }
}
