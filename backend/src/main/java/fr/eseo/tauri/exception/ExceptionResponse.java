package fr.eseo.tauri.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {

	@JsonProperty
	private String requestPath;

	@JsonProperty
	private String message;

	public ExceptionResponse(Exception exception, HttpServletRequest request) {
		this.requestPath = request.getServletPath();
		this.message = exception.getMessage();
	}

}
