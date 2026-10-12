package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID sagaId,
        UUID orderId,
        String failureReason
) implements Serializable {}
