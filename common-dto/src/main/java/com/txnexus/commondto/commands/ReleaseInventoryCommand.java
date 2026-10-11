package com.txnexus.commondto.commands;

import java.io.Serializable;
import java.util.UUID;

public record ReleaseInventoryCommand(
        UUID sagaId,
        UUID orderId,
        String productId,
        int quantity
) implements Serializable {}
