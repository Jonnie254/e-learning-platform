package com.jonnie.elearning.kafka.chat;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ChatConfigTopic {
    @Bean
    public NewTopic chatTopic(){
        return TopicBuilder
                .name("chat-topic")
                .build();
    }
}
