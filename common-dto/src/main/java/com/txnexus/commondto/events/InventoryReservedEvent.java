package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID sagaId,
        UUID orderId
) implements Serializable {}
