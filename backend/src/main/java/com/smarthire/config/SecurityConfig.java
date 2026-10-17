package com.smarthire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Enable CORS
            .cors(Customizer.withDefaults())

            // Disable CSRF because we are using JWT
            .csrf(csrf -> csrf.disable())

            // JWT based authentication
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // IMPORTANT:
                // Allow browser CORS preflight requests
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // Public registration
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users"
                ).permitAll()

                // Public login
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users/login"
                ).permitAll()

                // Current user can read their own profile via /api/users/me.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/users/me"
                ).authenticated()

                // Only administrators can browse the user directory.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/users",
                    "/api/users/{id}",
                    "/api/users/email/**"
                ).hasRole("ADMIN")

                // Admin
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // Recruiter
                .requestMatchers(
                    "/api/recruiters/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/jobs"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                // Job Seeker
                .requestMatchers(
                    "/api/job-seekers/**"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    "/api/job-seeker/**"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    "/api/job-seeker/resume/**"
                ).hasRole("JOB_SEEKER")

                // Applications
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/applications"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/my-applications"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/applications/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/job/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/*"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/applications/**"
                ).hasRole("JOB_SEEKER")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // JWT filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}