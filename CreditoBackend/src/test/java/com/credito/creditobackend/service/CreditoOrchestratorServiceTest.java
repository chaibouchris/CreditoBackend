package com.credito.creditobackend.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
public class CreditoOrchestratorServiceTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private CreditoOrchestratorService orchestratorService;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void resetMockServer() throws IOException {
        mockWebServer.shutdown();
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
    }

    // Test 1 - Verifies a successful service call
    // This test checks if a successful request to the external service returns the correct response.
    @Test
    public void testSuccessfulServiceCall() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"customerId\":\"12345\",\"creditScore\":750}")
                .addHeader("Content-Type", "application/json"));

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("customerId", "12345");

        Map<String, Object> response = orchestratorService.invokeService("scoring", requestData);

        assertEquals("12345", response.get("customerId"));
        assertEquals(750, response.get("creditScore"));
    }

    // Test 2 - Verifies retry mechanism on server error (500)
    // This test ensures that the system retries 3 times when a 500 Internal Server Error occurs.
    @Test
    public void testRetryOnServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("customerId", "99999");

        Exception exception = assertThrows(Exception.class, () -> {
            orchestratorService.invokeService("scoring", requestData);
        });

        assertEquals("Service scoring failed after 3 attempts", exception.getMessage());
        assertEquals(3, mockWebServer.getRequestCount(), "The system should retry 3 times on server error (500)");
    }

    // Test 3 - Verifies stopping retries on 404 Not Found error
    // This test checks that the system does not retry when a 404 Not Found error is encountered.
    @Test
    public void testStopRetryOnNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\":\"Not Found\"}"));

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("customerId", "00000");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            orchestratorService.invokeService("scoring", requestData);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); // Verifies 404 status
        assertEquals(1, mockWebServer.getRequestCount(), "The system should not retry on 404 errors");
    }

    // Test 4 - Verifies that the recover method is invoked after retry failures
    // This test ensures that the recover method is called after exhausting all retry attempts on repeated failures.
    @Test
    public void testRecoverMethodIsCalled() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("customerId", "11111");

        Exception exception = assertThrows(Exception.class, () -> {
            orchestratorService.invokeService("scoring", requestData);
        });

        assertEquals("Service scoring failed after 3 attempts", exception.getMessage());
    }
}
