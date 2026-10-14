package com.txnexus.commondto.commands;

import java.io.Serializable;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID sagaId,
        UUID orderId,
        double amount
) implements Serializable {}
