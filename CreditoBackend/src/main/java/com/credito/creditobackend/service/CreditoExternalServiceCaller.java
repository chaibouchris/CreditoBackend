package com.credito.creditobackend.service;

import com.credito.creditobackend.exception.ServiceInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class CreditoExternalServiceCaller {
    private static final Logger logger = LoggerFactory.getLogger(CreditoExternalServiceCaller.class);
    private final RestTemplate restTemplate;

    public CreditoExternalServiceCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> callExternalService(String serviceName, String serviceUrl,
                                                   Map<String, Object> requestData, RetryContext context) throws Exception {
        logger.info("Invoking external service: {} | URL: {}", serviceName, serviceUrl);
        try {
            Map<String, Object> response = restTemplate.postForObject(serviceUrl, requestData, Map.class);
            return Optional.ofNullable(response)
                    .filter(res -> !res.isEmpty())
                    .orElseThrow(() -> new ServiceInvocationException(
                            String.format("Empty response from %s", serviceName), new Exception("Empty response")));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("404 Not Found. Not retrying.");
                context.setExhaustedOnly();
            }
            throw e;
        }
    }
}
