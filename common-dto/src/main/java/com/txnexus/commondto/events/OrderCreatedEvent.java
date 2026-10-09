package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID sagaId,
        UUID orderId,
        String customerId,
        String productId,
        int quantity,
        double totalAmount
) implements Serializable {}
