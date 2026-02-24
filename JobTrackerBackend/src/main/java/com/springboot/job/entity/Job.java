package com.springboot.job.entity;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
	@Id
	@Column(name = "job_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int jobId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "company_name", nullable = false)
	private String companyName;

	@Column(name = "job_title", nullable = false)
	private String jobTitle;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "application_date")
	private LocalDate applicationDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@Column(name = "salary", nullable = true)
	private Integer salary;

	@Min(1)
	@Max(5)
	@Column(name = "priority")
	private int priority;

	@Column(name = "notes", nullable = true, length = 1000)
	private String notes;

	@Column(name = "job_description", nullable = true, length = 2000)
	private String jobDescription;
	
	@Column(name = "last_updated")
	private LocalDate lastUpdated;

}
