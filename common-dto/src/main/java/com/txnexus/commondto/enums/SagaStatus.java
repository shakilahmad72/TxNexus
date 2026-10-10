package com.txnexus.commondto.enums;

public enum SagaStatus {
    STARTED,
    INVENTORY_RESERVED,
    INVENTORY_RESERVATION_FAILED,
    PAYMENT_AUTHORIZED,
    PAYMENT_FAILED,
    COMPENSATING,
    COMPLETED
}
