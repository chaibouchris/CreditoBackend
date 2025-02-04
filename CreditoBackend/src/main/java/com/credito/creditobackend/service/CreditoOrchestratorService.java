package com.credito.creditobackend.service;

import com.credito.creditobackend.config.CreditoRetryConfig;
import com.credito.creditobackend.exception.ServiceInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@Service
public class CreditoOrchestratorService {
    private final CreditoRetryConfig retryConfig;
    private final CreditoServiceValidator serviceValidator;
    private final CreditoExternalServiceCaller externalServiceCaller;
    private final CreditoErrorHandler errorHandler;

    @Autowired
    public CreditoOrchestratorService(CreditoRetryConfig retryConfig,
                                      CreditoServiceValidator serviceValidator,
                                      CreditoExternalServiceCaller externalServiceCaller,
                                      CreditoErrorHandler errorHandler) {
        this.retryConfig = retryConfig;
        this.serviceValidator = serviceValidator;
        this.externalServiceCaller = externalServiceCaller;
        this.errorHandler = errorHandler;
    }

    public Map<String, Object> invokeService(String serviceName, Map<String, Object> requestData) throws Exception {
        String serviceUrl = serviceValidator.validateService(serviceName);
        return handleServiceInvocation(serviceName, requestData, serviceUrl);
    }

    private Map<String, Object> handleServiceInvocation(String serviceName, Map<String, Object> requestData, String serviceUrl) throws Exception {
        try {
            return retryConfig.retryTemplate().execute(context ->
                    externalServiceCaller.callExternalService(serviceName, serviceUrl, requestData, context));
        } catch (HttpServerErrorException | ResourceAccessException e) {
            errorHandler.handleRecovery(e, serviceName);
            throw e;
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceInvocationException(String.format("Error while calling service %s", serviceName), e);
        }
    }
}
