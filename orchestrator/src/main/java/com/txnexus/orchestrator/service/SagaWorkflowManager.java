package com.txnexus.orchestrator.service;

import com.txnexus.commondto.commands.CancelOrderCommand;
import com.txnexus.commondto.commands.ProcessPaymentCommand;
import com.txnexus.commondto.commands.ReleaseInventoryCommand;
import com.txnexus.commondto.commands.ReserveInventoryCommand;
import com.txnexus.commondto.enums.SagaStatus;
import com.txnexus.commondto.events.OrderCreatedEvent;
import com.txnexus.orchestrator.entity.SagaInstance;
import com.txnexus.orchestrator.kafka.producer.CommandProducer;
import com.txnexus.orchestrator.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaWorkflowManager {

    private final SagaInstanceRepository sagaRepository;
    private final CommandProducer commandProducer;

    // 1. Order Created -> Start saga -> Reserve Inventory
    @Transactional
    public void startSaga(OrderCreatedEvent event) {
        log.info("Starting Saga for Order: {}", event.orderId());

        SagaInstance saga = SagaInstance.builder()
                .sagaId(event.sagaId())
                .orderId(event.orderId())
                .customerId(event.customerId())
                .productId(event.productId())
                .quantity(event.quantity())
                .totalAmount(event.totalAmount())
                .status(SagaStatus.STARTED)
                .build();

        sagaRepository.save(saga);

        ReserveInventoryCommand command = new ReserveInventoryCommand(
                event.sagaId(),
                event.orderId(),
                event.productId(),
                event.quantity()
        );
        commandProducer.sendCommand("inventory.commands", event.orderId(), command);
    }

    // 2. Inventory Reserved -> Process Payment
    @Transactional
    public void onInventoryReserved(UUID sagaId) {
        SagaInstance saga = sagaRepository.findById(sagaId).orElseThrow();
        saga.setStatus(SagaStatus.INVENTORY_RESERVED);
        sagaRepository.save(saga);

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                saga.getSagaId(),
                saga.getOrderId(),
                saga.getTotalAmount()
        );
        commandProducer.sendCommand("payment.commands", saga.getOrderId(), command);
    }

    // 3. Payment Authorized -> Complete Saga
    @Transactional
    public void onPaymentAuthorized(UUID sagaId) {
        SagaInstance saga = sagaRepository.findById(sagaId).orElseThrow();
        saga.setStatus(SagaStatus.COMPLETED);
        sagaRepository.save(saga);
        log.info("Saga {} COMPLETED successfully for Order {}", sagaId, saga.getOrderId());
    }

    // 4. Payment Failed -> Trigger Compensation (Release stock & Cancel order)
    @Transactional
    public void onPaymentFailed(UUID sagaId, String reason) {
        SagaInstance saga = sagaRepository.findById(sagaId).orElseThrow();
        saga.setStatus(SagaStatus.COMPENSATING);
        saga.setFailureReason(reason);
        sagaRepository.save(saga);

        log.warn("Payment failed for Saga {}. Triggering inventory compensation...", sagaId);

        // Compensation 1: Release reserved inventory
        ReleaseInventoryCommand releaseCommand = new ReleaseInventoryCommand(
                saga.getSagaId(),
                saga.getOrderId(),
                saga.getProductId(),
                saga.getQuantity()
        );
        commandProducer.sendCommand("inventory.commands", saga.getOrderId(), releaseCommand);

        // Compensation 2: Cancel order in order-service
        CancelOrderCommand cancelCommand = new CancelOrderCommand(
                saga.getSagaId(),
                saga.getOrderId(),
                reason
        );
        commandProducer.sendCommand("order.commands", saga.getOrderId(), cancelCommand);
    }
}
