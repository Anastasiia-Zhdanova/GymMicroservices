package com.company.gym.config;

import com.company.gym.dto.request.LoginRequest;
import com.company.gym.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUsernamePasswordAuthenticationFilterTest {

    private static class MockServletInputStream extends ServletInputStream {
        private final InputStream buffer;

        public MockServletInputStream(String content) {
            this.buffer = new ByteArrayInputStream(content.getBytes());
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) { /* NOP */ }
    }


    @Mock
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    private CustomUsernamePasswordAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final String TEST_USERNAME = "test.user";
    private final String TEST_PASSWORD = "testpass";
    private final String JSON_BODY = "{\"username\":\"" + TEST_USERNAME + "\", \"password\":\"" + TEST_PASSWORD + "\"}";

    @BeforeEach
    void setUp() throws Exception {
        filter = new CustomUsernamePasswordAuthenticationFilter(authService, objectMapper, authenticationManager);
    }

    @Test
    void attemptAuthentication_Success() throws IOException {
        final LoginRequest validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername(TEST_USERNAME);
        validLoginRequest.setPassword(TEST_PASSWORD);

        when(request.getInputStream()).thenReturn(new MockServletInputStream(JSON_BODY));

        when(objectMapper.readValue(any(ServletInputStream.class), eq(LoginRequest.class)))
                .thenReturn(validLoginRequest);

        when(authService.authenticateUser(TEST_USERNAME, TEST_PASSWORD)).thenReturn(true);

        Authentication result = filter.attemptAuthentication(request, response);

        assertTrue(result.isAuthenticated());
        assertEquals(TEST_USERNAME, ((UserDetails) result.getPrincipal()).getUsername());
    }

    @Test
    void attemptAuthentication_FailsOnBadCredentials_AuthServiceFails() throws IOException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        when(request.getInputStream()).thenReturn(new MockServletInputStream(JSON_BODY));
        when(objectMapper.readValue(any(ServletInputStream.class), eq(LoginRequest.class)))
                .thenReturn(loginRequest);

        when(authService.authenticateUser(TEST_USERNAME, TEST_PASSWORD)).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> filter.attemptAuthentication(request, response),
                "Should throw BadCredentialsException when AuthService returns false.");
    }

    @Test
    void attemptAuthentication_FailsOnInvalidJsonBody() throws IOException {
        when(request.getInputStream()).thenReturn(new MockServletInputStream("invalid"));
        when(objectMapper.readValue(any(ServletInputStream.class), eq(LoginRequest.class)))
                .thenThrow(new IOException("Invalid JSON"));

        assertThrows(BadCredentialsException.class,
                () -> filter.attemptAuthentication(request, response),
                "Should throw BadCredentialsException on IO/JSON parsing error.");
    }

    @Test
    void attemptAuthentication_HandlesNullUsernameAndPassword() throws IOException {
        LoginRequest loginRequest = new LoginRequest();

        when(request.getInputStream()).thenReturn(new MockServletInputStream("{}"));
        when(objectMapper.readValue(any(ServletInputStream.class), eq(LoginRequest.class)))
                .thenReturn(loginRequest);

        when(authService.authenticateUser("", "")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> filter.attemptAuthentication(request, response),
                "Should throw BadCredentialsException if authentication fails with null/empty fields.");
    }
}