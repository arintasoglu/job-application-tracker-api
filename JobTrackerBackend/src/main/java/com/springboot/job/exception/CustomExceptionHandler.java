package com.springboot.job.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(value = { ApplicationNotFoundException.class })
	public ResponseEntity<?> handleCustomException(ApplicationNotFoundException ex, WebRequest request) {

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("timestamp", new Date());
		responseBody.put("message", ex.getMessage());
		responseBody.put("path", request.getDescription(false));
		responseBody.put("status", HttpStatus.NOT_FOUND.value());

		return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

}
