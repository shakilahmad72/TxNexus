package com.txnexus.orchestrator.dto;

import com.txnexus.commondto.enums.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStateResponse {
    private UUID sagaId;
    private UUID orderId;
    private SagaStatus status;
    private String customerId;
    private String productId;
    private Integer quantity;
    private Double totalAmount;
    private String failureReason;
    private Instant createdAt;
    private Instant updateAt;
}
