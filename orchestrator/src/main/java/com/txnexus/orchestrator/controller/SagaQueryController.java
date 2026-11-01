package com.txnexus.orchestrator.controller;

import com.txnexus.orchestrator.dto.SagaStateResponse;
import com.txnexus.orchestrator.service.SagaWorkflowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
public class SagaQueryController {

    private final SagaWorkflowManager sagaWorkflowManager;

    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaStateResponse> getSagaById(@PathVariable UUID sagaId) {
        return ResponseEntity.ok(sagaWorkflowManager.getSagaById(sagaId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<SagaStateResponse> getSagaByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(sagaWorkflowManager.getSagaByOrderId(orderId));
    }
}
