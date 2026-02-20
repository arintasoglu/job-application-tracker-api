package com.springboot.job.dto;

import java.time.LocalDate;

import com.springboot.job.entity.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

	@NotBlank(message = "Company name is required")
	private String companyName;

	@NotBlank(message = "Job title is required")
	private String jobTitle;

	@NotBlank(message = "Location is required")
	private String location;

	private LocalDate applicationDate;
	
    
	private Status status;

	@Min(value = 1, message = "Priority must be at least 1")
	@Max(value = 5, message = "Priority must be at most 5")
	private int priority;
	
    @Min(value = 0, message = "Salary must be a positive number")
	private Integer salary;

	private String notes;

	private String jobDescription;
}
