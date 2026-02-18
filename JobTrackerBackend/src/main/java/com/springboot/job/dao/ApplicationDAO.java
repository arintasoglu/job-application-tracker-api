package com.springboot.job.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.springboot.job.entity.Job;

public interface ApplicationDAO extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

}
