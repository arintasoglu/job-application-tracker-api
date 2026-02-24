package com.springboot.job.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.springboot.job.entity.Job;
import com.springboot.job.entity.JobStatusHistory;

public interface JobStatusHistoryDAO extends JpaRepository<JobStatusHistory, Integer>{
	

	List<JobStatusHistory> findByJobOrderByChangedAtDesc(Job job);
	
	

}
