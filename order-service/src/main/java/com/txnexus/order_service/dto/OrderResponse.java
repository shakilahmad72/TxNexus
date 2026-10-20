package com.txnexus.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private Double totalAmount;
    private OrderStatus status;
    private Instant createdAt;
}
