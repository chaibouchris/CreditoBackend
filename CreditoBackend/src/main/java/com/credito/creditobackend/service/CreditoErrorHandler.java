package com.credito.creditobackend.service;

import com.credito.creditobackend.config.CreditoRetryConfig;
import com.credito.creditobackend.exception.ServiceInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditoErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(CreditoErrorHandler.class);
    private final CreditoRetryConfig retryConfig;

    public CreditoErrorHandler(CreditoRetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public void handleRecovery(Exception e, String serviceName) throws Exception {
        logger.error("Failed to call service {} after {} attempts due to: {}",
                serviceName, retryConfig.getMaxRetries(), e.getMessage());
        throw new ServiceInvocationException(
                String.format("Service %s failed after %d attempts", serviceName, retryConfig.getMaxRetries()), e);
    }
}
