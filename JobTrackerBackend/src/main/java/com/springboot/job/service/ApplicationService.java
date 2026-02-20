package com.springboot.job.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.springboot.job.dao.ApplicationDAO;
import com.springboot.job.dao.JobStatusHistoryDAO;
import com.springboot.job.dto.ApplicationCreateRequest;
import com.springboot.job.dto.ApplicationResponse;
import com.springboot.job.dto.OverviewResponse;
import com.springboot.job.entity.Job;
import com.springboot.job.entity.JobStatusHistory;
import com.springboot.job.entity.Status;
import com.springboot.job.exception.ApplicationNotFoundException;
import com.springboot.job.specification.JobSpecification;

@Service
public class ApplicationService {
	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private JobStatusHistoryDAO historyDAO;

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

		Status oldStatus = job.getStatus();
		Status newStatus = status;

		if (newStatus == null) {
			throw new IllegalArgumentException("status must not be null");
		}

		if (oldStatus == newStatus) {
			return toResponse(job);
		}

		job.setStatus(newStatus);
		Job updatedJob = applicationDAO.save(job);

		JobStatusHistory history = new JobStatusHistory();
		history.setJob(job);
		history.setFromStatus(oldStatus);
		history.setToStatus(newStatus);
		history.setChangedAt(LocalDateTime.now());

		historyDAO.save(history);

		return toResponse(updatedJob);

	}

	public List<JobStatusHistory> getStatusHistory(int jobId) {
		return historyDAO.findByJob_JobIdOrderByChangedAtDesc(jobId);
	}

	public OverviewResponse getOverview() {
		OverviewResponse response = new OverviewResponse();

		List<Job> jobs = applicationDAO.findAll();

		int total = jobs.size();
		int draftCount = 0;
		int appliedCurrent = 0;
		int interviewingCurrent = 0;
		int offeredCurrent = 0;
		int acceptedCurrent = 0;
		int rejectedCurrent = 0;
		int withdrawnCurrent = 0;

		int everApplied = 0;
		int everInterviewing = 0;
		int everOffered = 0;
		int everAccepted = 0;

		for (Job job : jobs) {

			Status s = job.getStatus();
			if (s == Status.DRAFT)
				draftCount++;
			else if (s == Status.APPLIED)
				appliedCurrent++;
			else if (s == Status.INTERVIEWING)
				interviewingCurrent++;
			else if (s == Status.OFFERED)
				offeredCurrent++;
			else if (s == Status.ACCEPTED)
				acceptedCurrent++;
			else if (s == Status.REJECTED)
				rejectedCurrent++;
			else if (s == Status.WITHDRAWN)
				withdrawnCurrent++;

			List<Status> history = getStatusHistoryForJob(job.getJobId());

			if (history.isEmpty() && s != null) {
				history = List.of(s);
			}

			if (historyContains(history, Status.APPLIED))
				everApplied++;
			if (historyContains(history, Status.INTERVIEWING))
				everInterviewing++;
			if (historyContains(history, Status.OFFERED))
				everOffered++;
			if (historyContains(history, Status.ACCEPTED))
				everAccepted++;
		}

		response.setTotalApplications(total);
		response.setDraftApplications(draftCount);
		response.setInterviewApplications(interviewingCurrent);
		response.setAcceptedApplications(acceptedCurrent);
		response.setRejectedApplications(rejectedCurrent);

		if (everOffered > 0) {
			response.setAcceptanceRate(round2((double) everAccepted / everOffered * 100));
		} else {
			response.setAcceptanceRate(0);
		}

		if (everApplied > 0) {
			response.setInterviewRate(round2((double) everInterviewing / everApplied * 100));
		} else {
			response.setInterviewRate(0);
		}

		return response;
	}

	private boolean historyContains(List<Status> history, Status target) {
		for (Status s : history) {
			if (s == target)
				return true;
		}
		return false;
	}

	private double round2(double value) {
		return Math.round(value * 100.0) / 100.0;
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

	public List<Status> getStatusHistoryForJob(int id) {
		List<JobStatusHistory> list = getStatusHistory(id);

		if (list.isEmpty()) {
			return List.of();
		}

		List<Status> result = new ArrayList<>();

		result.add(list.get(0).getFromStatus());

		for (JobStatusHistory h : list) {
			result.add(h.getToStatus());
		}

		return result;
	}

}
