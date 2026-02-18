package com.springboot.job.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.springboot.job.dao.ApplicationDAO;
import com.springboot.job.dto.ApplicationCreateRequest;
import com.springboot.job.dto.ApplicationResponse;
import com.springboot.job.entity.Job;
import com.springboot.job.entity.Status;
import com.springboot.job.exception.ApplicationNotFoundException;
import com.springboot.job.specification.JobSpecification;

@Service
public class ApplicationService {
	@Autowired
	private ApplicationDAO applicationDAO;

	public ApplicationResponse createApplication(ApplicationCreateRequest request) {
		Job job = new Job();
		job.setCompanyName(request.getCompanyName());
		job.setJobTitle(request.getJobTitle());
		job.setLocation(request.getLocation());
		job.setApplicationDate(request.getApplicationDate());
		job.setStatus(request.getStatus());
		job.setSalary(request.getSalary());
		job.setPriority(request.getPriority());
		job.setNotes(request.getNotes());
		job.setJobDescription(request.getJobDescription());

		Job savedApplication = applicationDAO.save(job);

		return toResponse(savedApplication);
	}

	public ApplicationResponse getApplicationById(int id) {
		Job job = applicationDAO.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));

		return toResponse(job);
	}

	public List<ApplicationResponse> getAllApplications(int page, int size, String sortBy, String sortDir,
			String search) {

		
		String safeSortBy = "applicationDate";
		if ("priority".equalsIgnoreCase(sortBy)) {
			safeSortBy = "priority";
		} else if ("applicationDate".equalsIgnoreCase(sortBy)) {
			safeSortBy = "applicationDate";
		}

		Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

		
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, safeSortBy));

		
		Specification<Job> spec = JobSpecification.getSpecification(search);

		List<Job> jobs = applicationDAO.findAll(spec, pageable).getContent();
		for (Job job : jobs) {
			System.out.println("Job: " + job.getCompanyName() + ", " + job.getJobTitle());
		}
		List<ApplicationResponse> responses = new ArrayList<>();

		for (Job job : jobs) {
			responses.add(toResponse(job));
		}

		return responses;
	}

	public void deleteApplication(int id) {
		if (!applicationDAO.existsById(id)) {
			throw new ApplicationNotFoundException(id);
		}
		applicationDAO.deleteById(id);
	}

	public ApplicationResponse updateApplicationStatus(int id, Status status) {
		Job job = applicationDAO.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
		job.setStatus(status);
		Job updatedJob = applicationDAO.save(job);
		return toResponse(updatedJob);
	}

	public ApplicationResponse toResponse(Job job) {
		ApplicationResponse response = new ApplicationResponse();
		response.setId(job.getJobId());
		response.setCompanyName(job.getCompanyName());
		response.setJobTitle(job.getJobTitle());
		response.setLocation(job.getLocation());
		response.setApplicationDate(job.getApplicationDate());
		response.setStatus(job.getStatus());
		response.setPriority(job.getPriority());
		response.setSalary(job.getSalary());
		response.setNotes(job.getNotes());
		return response;
	}

}
