package com.springboot.job.controller;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.job.dto.LoginRequest;
import com.springboot.job.dto.LoginResponse;
import com.springboot.job.entity.User;
import com.springboot.job.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping("/register")
	public void register(@RequestBody @Validated LoginRequest request) {
		userService.registerUser(request.getEmail(), request.getPassword());
	
	}
	
	@PostMapping("/login")
	public String login( @RequestBody @Validated LoginRequest request) {
	   return userService.verify(request.getEmail(), request.getPassword());
	}
	


}
