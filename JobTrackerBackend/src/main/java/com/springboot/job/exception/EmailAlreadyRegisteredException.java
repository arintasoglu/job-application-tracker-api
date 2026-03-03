package com.springboot.job.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
		private String message;

	public EmailAlreadyRegisteredException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
