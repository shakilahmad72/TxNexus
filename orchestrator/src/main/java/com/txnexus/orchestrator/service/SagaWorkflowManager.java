package com.txnexus.orchestrator.service;

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


}
