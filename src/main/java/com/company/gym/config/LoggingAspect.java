package com.company.gym.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Around("execution(public * com.company.gym.controller.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID_KEY, transactionId);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        String endpoint = request.getMethod() + " " + request.getRequestURI();

        logger.info("REST CALL START: [Endpoint: {} | Method: {} | Request: {}]", endpoint, methodName, request.getParameterMap());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("REST CALL FAILED: [Endpoint: {} | Method: {} | Duration: {}ms | Error: {}] -> Stack Trace in DEBUG",
                    endpoint, methodName, executionTime, ex.getMessage(), ex);
            throw ex;
        } finally {
            if (MDC.get(TRANSACTION_ID_KEY) != null) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("REST CALL END: [Endpoint: {} | Method: {} | Duration: {}ms] -> Response Status: OK/Error",
                        endpoint, methodName, executionTime);
                MDC.remove(TRANSACTION_ID_KEY);
            }
        }
        return result;
    }
}