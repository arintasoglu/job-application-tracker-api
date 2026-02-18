package com.springboot.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobTrackerBackendApplication.class, args);
		System.out.println("Job Tracker Backend is running...");
	}

}
