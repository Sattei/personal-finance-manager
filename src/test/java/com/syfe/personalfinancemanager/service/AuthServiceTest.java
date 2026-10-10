package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.auth.LoginRequest;
import com.syfe.personalfinancemanager.dto.auth.RegisterRequest;
import com.syfe.personalfinancemanager.dto.auth.RegisterResponse;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.ConflictException;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerSuccess() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setPhoneNumber("+1234567890");

        User savedUser = mock(User.class);

        when(userRepository.existsByUsername("user@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(savedUser.getId())
                .thenReturn(1L);

        RegisterResponse response =
                authService.register(request);

        assertEquals(
                "User registered successfully",
                response.getMessage()
        );

        assertEquals(
                1L,
                response.getUserId()
        );

        verify(userRepository)
                .existsByUsername("user@example.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void registerDuplicateUsername() {

        RegisterRequest request = new RegisterRequest();

        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setPhoneNumber("+1234567890");

        when(userRepository.existsByUsername("user@example.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> authService.register(request)
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginSuccess() {

        LoginRequest request =
                new LoginRequest(
                        "user@example.com",
                        "password123"
                );

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        Authentication result =
                authService.login(request);

        assertSame(authentication, result);

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );
    }

    @Test
    void loginInvalidCredentials() {

        LoginRequest request =
                new LoginRequest(
                        "user@example.com",
                        "wrongpassword"
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException(
                        "Bad credentials"
                )
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );
    }
}