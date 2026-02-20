package com.springboot.job.dto;

import java.time.LocalDate;

import com.springboot.job.entity.Status;

import lombok.Data;

@Data
public class ApplicationResponse {

	private int id;
	private String companyName;
	private String jobTitle;
	private String location;
	private LocalDate applicationDate;
	private Status status;
	private int priority;
	private Integer salary;
	private String notes;
	private String jobDescription;
	private LocalDate lastUpdated;

}
