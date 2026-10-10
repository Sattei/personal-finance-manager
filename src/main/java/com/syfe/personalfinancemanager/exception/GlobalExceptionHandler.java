package com.syfe.personalfinancemanager.exception;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageResponse> handleBadRequest(
            BadRequestException exception) {

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(
            ResourceNotFoundException exception) {

        return ResponseEntity
                .status(404)
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<MessageResponse> handleConflict(
            ConflictException exception) {

        return ResponseEntity
                .status(409)
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<MessageResponse> handleForbidden(
            ForbiddenException exception) {

        return ResponseEntity
                .status(403)
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessageResponse> handleAuthentication(
            AuthenticationException exception) {

        return ResponseEntity
                .status(401)
                .body(new MessageResponse(
                        "Invalid username or password"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> handleMalformedRequest(
            HttpMessageNotReadableException exception) {

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(
                        "Malformed request body"
                ));
    }
}