package com.syfe.personalfinancemanager.config;

import com.syfe.personalfinancemanager.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Configures authentication, session-backed security, and access rules for the application.
 */
@Configuration
public class SecurityConfig {

    /**
     * Provides the password encoder used to store and verify user passwords.
     *
     * @return a BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates the provider that authenticates users with application user details.
     *
     * @param userDetailsService service used to load users by username
     * @param passwordEncoder encoder used to verify passwords
     * @return the configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    /**
     * Exposes Spring Security's authentication manager.
     *
     * @param configuration current authentication configuration
     * @return the configured authentication manager
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Stores the security context in the user's HTTP session.
     *
     * @return the session-based security context repository
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * Defines public authentication endpoints and requires authentication elsewhere.
     *
     * @param http security builder for the application
     * @param securityContextRepository repository used to persist authenticated contexts
     * @return the configured security filter chain
     * @throws Exception if the filter chain cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository)
            throws Exception {

        http
                .csrf(csrf ->
                        csrf.disable()
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .securityContext(context ->
                        context.securityContextRepository(
                                securityContextRepository
                        )
                );

        return http.build();
    }
}
