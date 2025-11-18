package com.example.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentCardNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(PaymentCardNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(UserNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CardLimitExceededException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(CardLimitExceededException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(EmailAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(CardAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Throwable ex, HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.create(ex, status, ex.getMessage()));
    }
}
