package com.credito.creditobackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreditoServiceRegistry {
    private final Map<String, String> serviceMap;
    private static final Logger logger = LoggerFactory.getLogger(CreditoServiceRegistry.class);


    public CreditoServiceRegistry(@Value("${services.scoring}") String scoringUrl,
                                  @Value("${services.analyzer}") String analyzerUrl) {
        this.serviceMap = new HashMap<>();
        serviceMap.put("scoring", scoringUrl);
        serviceMap.put("analyzer", analyzerUrl);

        logger.info("Loaded service URLs:");
        logger.info("Scoring: {}", scoringUrl);
        logger.info("Analyzer: {}", analyzerUrl);
    }

    public String getServiceUrl(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
