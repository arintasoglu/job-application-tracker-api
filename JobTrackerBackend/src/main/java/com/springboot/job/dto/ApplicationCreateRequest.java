package com.springboot.job.dto;

import java.time.LocalDate;

import com.springboot.job.entity.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

	@NotBlank
	private String companyName;

	@NotBlank
	private String jobTitle;

	@NotBlank
	private String location;

	private LocalDate applicationDate;

	private Status status; 

	@Min(1)
	@Max(5)
	private int priority;

	private Integer salary;

	private String notes;

	private String jobDescription;
}
