package ASimulatorSystem.backend.controller;

import ASimulatorSystem.backend.dto.ApiDtos;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiDtos.ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("Invalid request.");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiDtos.ErrorResponse> badRequest(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiDtos.ErrorResponse> conflict(IllegalStateException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiDtos.ErrorResponse> serverError(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.");
    }

    private ResponseEntity<ApiDtos.ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiDtos.ErrorResponse(status.getReasonPhrase(), message, Instant.now()));
    }
}
