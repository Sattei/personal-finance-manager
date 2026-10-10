package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.auth.LoginRequest;
import com.syfe.personalfinancemanager.dto.auth.LoginResponse;
import com.syfe.personalfinancemanager.dto.auth.RegisterRequest;
import com.syfe.personalfinancemanager.dto.auth.RegisterResponse;
import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerSuccess() {

        RegisterRequest request = new RegisterRequest();

        RegisterResponse serviceResponse =
                new RegisterResponse(
                        "User registered successfully",
                        1L
                );

        when(authService.register(request))
                .thenReturn(serviceResponse);

        ResponseEntity<RegisterResponse> response =
                authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "User registered successfully",
                response.getBody().getMessage()
        );

        assertEquals(
                1L,
                response.getBody().getUserId()
        );

        verify(authService).register(request);
    }

    @Test
    void loginSuccess() {

        LoginRequest request =
                new LoginRequest(
                        "user@example.com",
                        "password123"
                );

        HttpServletRequest httpRequest =
                mock(HttpServletRequest.class);

        HttpServletResponse httpResponse =
                mock(HttpServletResponse.class);

        Authentication authentication =
                mock(Authentication.class);

        when(authService.login(request))
                .thenReturn(authentication);

        ResponseEntity<LoginResponse> response =
                authController.login(
                        request,
                        httpRequest,
                        httpResponse
                );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "Login successful",
                response.getBody().getMessage()
        );

        assertSame(
                authentication,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(securityContextRepository)
                .saveContext(
                        any(),
                        eq(httpRequest),
                        eq(httpResponse)
                );
    }

    @Test
    void logoutWithSession() {

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        ResponseEntity<MessageResponse> response =
                authController.logout(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "Logout successful",
                response.getBody().getMessage()
        );

        verify(session).invalidate();
    }

    @Test
    void logoutWithoutSession() {

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getSession(false))
                .thenReturn(null);

        ResponseEntity<MessageResponse> response =
                authController.logout(request);

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "Logout successful",
                response.getBody().getMessage()
        );
    }
}