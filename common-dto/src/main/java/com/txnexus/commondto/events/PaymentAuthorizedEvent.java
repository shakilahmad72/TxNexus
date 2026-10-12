package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record PaymentAuthorizedEvent(
        UUID sagaId,
        UUID orderId,
        String transactionId
) implements Serializable {}
