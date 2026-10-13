package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID sagaId,
        UUID orderId,
        String reason
) implements Serializable {}
