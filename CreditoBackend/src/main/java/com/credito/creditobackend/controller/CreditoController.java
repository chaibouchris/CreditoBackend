package com.credito.creditobackend.controller;

import com.credito.creditobackend.service.CreditoOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/service")
public class CreditoController {
    private final CreditoOrchestratorService orchestrator;

    public CreditoController(CreditoOrchestratorService orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/{serviceName}")
    public ResponseEntity<?> callService(@PathVariable String serviceName, @RequestBody Map<String, Object> requestData) {
        try {
            Map<String, Object> response = orchestrator.invokeService(serviceName, requestData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error invoking service: " + e.getMessage());
        }
    }
}
