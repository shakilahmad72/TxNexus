package com.txnexus.commondto.commands;

import java.io.Serializable;
import java.util.UUID;

public record CancelOrderCommand(
        UUID sagaId,
        UUID orderId,
        String reason
) implements Serializable {}
