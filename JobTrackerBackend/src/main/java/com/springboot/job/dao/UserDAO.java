package com.springboot.job.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.job.entity.User;

public interface UserDAO extends JpaRepository<User, Integer>{
	User findByEmail(String email);
	

}
