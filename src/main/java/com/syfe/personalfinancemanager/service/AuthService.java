package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.auth.LoginRequest;
import com.syfe.personalfinancemanager.dto.auth.RegisterRequest;
import com.syfe.personalfinancemanager.dto.auth.RegisterResponse;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.ConflictException;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for user registration
 * and authentication.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user after checking for duplicate usernames
     * and securely encoding the password.
     *
     * @param request registration information
     * @return registration response containing the created user ID
     * @throws ConflictException if the username already exists
     */
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                encodedPassword,
                request.getFullName(),
                request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                "User registered successfully",
                savedUser.getId()
        );
    }

    /**
     * Authenticates a user's login credentials.
     *
     * @param request login credentials
     * @return authenticated user information
     */
    public Authentication login(LoginRequest request) {

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    }
}