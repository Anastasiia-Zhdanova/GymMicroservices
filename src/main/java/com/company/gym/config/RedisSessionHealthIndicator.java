package com.company.gym.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisSession")
public class RedisSessionHealthIndicator implements HealthIndicator {

    private final StringRedisTemplate template;
    private static final String TEST_KEY = "health_check";

    public RedisSessionHealthIndicator(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Health health() {
        try {
            template.opsForValue().set(TEST_KEY, "OK");
            String result = template.opsForValue().get(TEST_KEY);
            template.delete(TEST_KEY);

            if ("OK".equals(result)) {
                return Health.up().withDetail("session_storage", "Available and functional").build();
            } else {
                return Health.down().withDetail("session_storage", "Not functional").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("error", "Redis connection failed").build();
        }
    }
}