package com.ecommerce.accountservice.exception;

import com.ecommerce.accountservice.exception.response.ExceptionResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(String.valueOf(ExceptionCode.NOT_FOUND))
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(exceptionResponse, ExceptionCode.NOT_FOUND.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }

    private ResponseEntity<Object> handleExceptionInternal(ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(createExceptionResponse(exceptionCode));
    }

    private ExceptionResponse createExceptionResponse(ExceptionCode exceptionCode) {
        return ExceptionResponse.builder()
                .code(exceptionCode.name())
                .message(exceptionCode.getMessage())
                .build();
    }
}
