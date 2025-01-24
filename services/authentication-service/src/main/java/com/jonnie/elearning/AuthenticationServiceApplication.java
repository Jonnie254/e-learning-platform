package com.jonnie.elearning;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableMongoAuditing
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.filename(".env")
				.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}
}


