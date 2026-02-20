package com.springboot.job.dto;

import lombok.Data;

@Data
public class OverviewResponse {
	private int totalApplications;
	private int draftApplications;
	private int interviewApplications;
	private int acceptedApplications;
	private int rejectedApplications;
	private double acceptanceRate;
	private double interviewRate;
	

}
