package com.springboot.job.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import com.springboot.job.entity.Job;
import com.springboot.job.entity.Status;

public interface ApplicationDAO extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

	Page<Job> findByUserEmail(String email, Pageable pageable);
	List<Job> findByUserEmail(String email);

	Optional<Job> findByJobIdAndUserEmail(int jobId, String email);

	void deleteByJobIdAndUserEmail(int jobId, String email);

	boolean existsByJobIdAndUserEmail(int jobId, String email);

}
