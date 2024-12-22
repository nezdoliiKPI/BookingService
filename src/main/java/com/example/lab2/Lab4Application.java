package com.example.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lab4Application {
	public static void main(String[] args) {
		SpringApplication.run(Lab4Application.class, args);
	}
}
