package com.company.gym.config;

import com.company.gym.dto.request.LoginRequest;
import com.company.gym.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public CustomUsernamePasswordAuthenticationFilter(AuthService authService,
                                                      ObjectMapper objectMapper,
                                                      AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        setAuthenticationManager(authenticationManager);
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/v1/auth/login", "POST"));
        setSecurityContextRepository(new HttpSessionSecurityContextRepository());

        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
        });

        this.setAuthenticationFailureHandler((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Authentication Failed\", \"message\": \"" + exception.getMessage() + "\"}"
            );
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            username = "";
            password = "";
        }

        if (authService.authenticateUser(username, password)) {
            UserDetails userDetails = User.withUsername(username)
                    .password("")
                    .roles("USER")
                    .build();

            return UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
        }

        throw new BadCredentialsException("Invalid username or password");
    }
}