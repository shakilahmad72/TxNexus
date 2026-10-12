package com.txnexus.commondto.events;

import java.io.Serializable;
import java.util.UUID;

public record InventoryReservationFailedEvent (
        UUID sagaId,
        UUID orderId,
        String reason
) implements Serializable {}
