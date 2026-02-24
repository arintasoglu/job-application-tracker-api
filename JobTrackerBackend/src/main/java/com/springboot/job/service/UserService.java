package com.springboot.job.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.job.dao.UserDAO;
import com.springboot.job.entity.User;
import com.springboot.job.entity.UserPrincipal;

@Service
public class UserService {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private JWTService jwtService;
	@Autowired
	AuthenticationManager authenticationManager;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public boolean isEmailRegistered(String email) {
		return userDAO.findByEmail(email) != null;
	}

	public void registerUser(String email, String password) {
		if (isEmailRegistered(email)) {
			throw new IllegalArgumentException("Email is already registered");
		}
		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		userDAO.save(user);
	}

	public boolean authenticateUser(String email, String password) {
		var user = userDAO.findByEmail(email);
		if (user == null) {
			return false;
		}
		return user.getPassword().equals(password); // In production, use hashed passwords!
	}

	public User findByEmail(String username) {
		return userDAO.findByEmail(username);
	}

	public String verify(String email, String password) {

		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		if (auth.isAuthenticated()) {
			return jwtService.generateToken(email);
		}

		return "failed";
	}

}
