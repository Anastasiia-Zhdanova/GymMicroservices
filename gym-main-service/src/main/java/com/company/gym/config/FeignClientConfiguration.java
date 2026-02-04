package com.company.gym.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // Получаем текущий HTTP запрос
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                // 1. Берем токен из заголовка Authorization и передаем дальше
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }

                // 2. Берем Transaction ID из MDC и передаем дальше (для логов)
                String transactionId = MDC.get("transactionId");
                if (transactionId != null) {
                    template.header("X-Transaction-Id", transactionId);
                }
            }
        };
    }
}