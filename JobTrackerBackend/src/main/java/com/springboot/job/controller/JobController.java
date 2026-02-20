package com.springboot.job.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.job.dao.JobStatusHistoryDAO;
import com.springboot.job.dto.ApplicationCreateRequest;
import com.springboot.job.dto.ApplicationResponse;
import com.springboot.job.dto.OverviewResponse;
import com.springboot.job.entity.JobStatusHistory;
import com.springboot.job.entity.Status;
import com.springboot.job.exception.ApplicationNotFoundException;
import com.springboot.job.service.ApplicationService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
	@Autowired
	private ApplicationService applicationService;

	@PostMapping("/create")
	public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
		return applicationService.createApplication(request);
	}

	@GetMapping("/applications/{id}")
	public ApplicationResponse getApplicationById(@PathVariable int id) {

		return applicationService.getApplicationById(id);

	}

	@GetMapping("/applications")
	public List<ApplicationResponse> getAllApplications(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "applicationDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir, @RequestParam(required = false) String search) {

		return applicationService.getAllApplications(page, size, sortBy, sortDir, search);

	}

	@DeleteMapping("/applications/{id}")
	public void deleteApplication(@PathVariable int id) {
		applicationService.deleteApplication(id);
	}

	@PostMapping("/applications/{id}/status")
	public ApplicationResponse updateApplicationStatus(@PathVariable int id, @RequestBody Status status) {

		return applicationService.updateApplicationStatus(id, status);
	}

	@GetMapping("/stats")
	public OverviewResponse getOverviewStats() {
		return applicationService.getOverview();
	}

	@GetMapping("/{id}/history")
	public List<Status> getHistory(@PathVariable("id") int id) {

		return applicationService.getStatusHistoryForJob(id);

	}
	@GetMapping("/applications/followups")
	public List<ApplicationResponse> getFollowUpApplications(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "applicationDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return applicationService.getFollowUpApplications(page, size, sortBy, sortDir);
	}

}
