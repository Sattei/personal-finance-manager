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
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            SecurityContextRepository securityContextRepository) {

        this.authService = authService;
        this.securityContextRepository = securityContextRepository;
    }

    /**
     * Registers a new user account.
     *
     * @param request validated registration data
     * @return the registration result and new user identifier
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                authService.register(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    /**
     * Authenticates a user, rotates any existing session ID,
     * and stores the resulting security context in the session.
     *
     * @param request validated login credentials
     * @param httpRequest current HTTP request
     * @param httpResponse current HTTP response
     * @return a successful login response
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication =
                authService.login(request);

        HttpSession session =
                httpRequest.getSession(false);

        if (session != null) {
            httpRequest.changeSessionId();
        }

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        LoginResponse response =
                new LoginResponse("Login successful");

        return ResponseEntity.ok(response);
    }

    /**
     * Invalidates the current session and clears the security context.
     *
     * @param request current HTTP request
     * @return a successful logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                new MessageResponse(
                        "Logout successful"
                )
        );
    }
}