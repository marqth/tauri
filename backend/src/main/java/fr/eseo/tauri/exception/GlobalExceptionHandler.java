package fr.eseo.tauri.exception;

import fr.eseo.tauri.util.CustomLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	public static final String UNAUTHORIZED_ACTION = "Unauthorized action";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleArgumentNotValidException(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	// Handle the exceptions related to bad parameters for the requests
	@ExceptionHandler(value = { IllegalArgumentException.class, NumberFormatException.class, ArrayIndexOutOfBoundsException.class,
			ServletRequestBindingException.class, HttpMessageNotReadableException.class, TypeMismatchException.class,
			HandlerMethodValidationException.class, EmptyResourceException.class })
	public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception exception, HttpServletRequest request) {
		return handleException(exception, request, HttpStatus.BAD_REQUEST);
	}

	// Handle the exceptions related to not found elements
	@ExceptionHandler(value = {NullPointerException.class, ResourceNotFoundException.class})
	public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception exception, HttpServletRequest request) {
		return handleException(exception, request, HttpStatus.NOT_FOUND);
	}

	// Handle the exceptions related to unauthorized actions
	@ExceptionHandler(value = {SecurityException.class})
	public ResponseEntity<ExceptionResponse> handleUnauthorizedException(Exception exception, HttpServletRequest request) {
		return handleException(exception, request, HttpStatus.UNAUTHORIZED);
	}

	// Handle all other exceptions
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ExceptionResponse> handleUnhandledExceptions(Exception exception, HttpServletRequest request) {
		return handleException(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request, HttpStatus status) {
		var response = new ExceptionResponse(e, request);
		CustomLogger.error(request.getServletPath());
		CustomLogger.error(e.getMessage());
		return ResponseEntity.status(status).body(response);
	}

}
