package com.company.gym.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private Signature signature;

    private static final String TRANSACTION_ID_KEY = "transactionId";

    @BeforeEach
    void setUp() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(request);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getParameterMap()).thenReturn(Collections.emptyMap());

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.testMethod()");

        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logAround_SuccessPath_MDCIsHandled() throws Throwable {
        final String EXPECTED_RESULT = "success";
        when(joinPoint.proceed()).thenReturn(EXPECTED_RESULT);

        Object result = loggingAspect.logAround(joinPoint);

        assertEquals(EXPECTED_RESULT, result);

        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logAround_FailurePath_LogsErrorAndRethrows() throws Throwable {
        final RuntimeException MOCK_EXCEPTION = new RuntimeException("Simulated failure");

        when(joinPoint.proceed()).thenThrow(MOCK_EXCEPTION);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> loggingAspect.logAround(joinPoint));
        assertEquals(MOCK_EXCEPTION, thrown);

        verify(joinPoint, times(1)).proceed();

        assertNull(MDC.get(TRANSACTION_ID_KEY), "MDC transactionId must be null after failure execution.");
    }
}