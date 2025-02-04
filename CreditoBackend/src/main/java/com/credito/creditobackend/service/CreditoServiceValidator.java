package com.credito.creditobackend.service;

import com.credito.creditobackend.config.CreditoServiceRegistry;
import com.credito.creditobackend.exception.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditoServiceValidator {
    private static final Logger logger = LoggerFactory.getLogger(CreditoServiceValidator.class);
    private final CreditoServiceRegistry serviceRegistry;

    public CreditoServiceValidator(CreditoServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public String validateService(String serviceName) {
        return Optional.ofNullable(serviceRegistry.getServiceUrl(serviceName))
                .orElseThrow(() -> {
                    logger.error("Service not found: {}", serviceName);
                    return new ServiceNotFoundException(String.format("Service %s not found", serviceName));
                });
    }
}
