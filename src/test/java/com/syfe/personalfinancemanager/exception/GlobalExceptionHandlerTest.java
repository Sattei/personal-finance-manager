package com.syfe.personalfinancemanager.exception;

import com.syfe.personalfinancemanager.dto.common.MessageResponse;

import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleBadRequest() {

        ResponseEntity<MessageResponse> response =
                handler.handleBadRequest(
                        new BadRequestException(
                                "Invalid request"
                        )
                );

        assertEquals(400, response.getStatusCode().value());

        assertEquals(
                "Invalid request",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleNotFound() {

        ResponseEntity<MessageResponse> response =
                handler.handleNotFound(
                        new ResourceNotFoundException(
                                "Not found"
                        )
                );

        assertEquals(404, response.getStatusCode().value());

        assertEquals(
                "Not found",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleConflict() {

        ResponseEntity<MessageResponse> response =
                handler.handleConflict(
                        new ConflictException(
                                "Already exists"
                        )
                );

        assertEquals(409, response.getStatusCode().value());

        assertEquals(
                "Already exists",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleForbidden() {

        ResponseEntity<MessageResponse> response =
                handler.handleForbidden(
                        new ForbiddenException(
                                "Forbidden"
                        )
                );

        assertEquals(403, response.getStatusCode().value());

        assertEquals(
                "Forbidden",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleAuthentication() {

        ResponseEntity<MessageResponse> response =
                handler.handleAuthentication(
                        new BadCredentialsException(
                                "Bad credentials"
                        )
                );

        assertEquals(401, response.getStatusCode().value());

        assertEquals(
                "Invalid username or password",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleValidation() {

        MethodArgumentNotValidException exception =
                mock(
                        MethodArgumentNotValidException.class
                );

        BindingResult bindingResult =
                mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "request",
                        "username",
                        "Username is required"
                );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(
                        java.util.List.of(fieldError)
                );

        ResponseEntity<MessageResponse> response =
                handler.handleValidation(exception);

        assertEquals(400, response.getStatusCode().value());

        assertEquals(
                "Username is required",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleMalformedRequest() {

        HttpMessageNotReadableException exception =
                mock(
                        HttpMessageNotReadableException.class
                );

        ResponseEntity<MessageResponse> response =
                handler.handleMalformedRequest(exception);

        assertEquals(400, response.getStatusCode().value());

        assertEquals(
                "Malformed request body",
                response.getBody().getMessage()
        );
    }
}