package com.company.gym.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisSessionHealthIndicatorTest {

    @Mock
    private StringRedisTemplate template;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisSessionHealthIndicator indicator;

    @Test
    void health_ReturnsUp_WhenRedisIsFunctional() {
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("OK");

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Available and functional", health.getDetails().get("session_storage"));
        verify(template, times(1)).delete(anyString());
    }

    @Test
    void health_ReturnsDown_WhenRedisConnectionFails() {
        when(template.opsForValue()).thenThrow(new RuntimeException("Connection timed out"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
        assertEquals("Redis connection failed", health.getDetails().get("error"));
    }
}