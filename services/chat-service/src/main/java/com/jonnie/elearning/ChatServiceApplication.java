package com.jonnie.elearning;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SpringBootApplication
@EnableFeignClients
@EntityListeners({AuditingEntityListener.class})
public class ChatServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.filename(".env")
				.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
				entry.getValue()));
		SpringApplication.run(ChatServiceApplication.class, args);
	}
}
