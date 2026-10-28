package com.txnexus.orchestrator.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.txnexus.commondto.events.OrderCreatedEvent;
import com.txnexus.orchestrator.service.SagaWorkflowManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final SagaWorkflowManager workflowManager;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = "order.events", groupId = "saga-orchestrator-group")
    public void consume(String messagePayload) {
        OrderCreatedEvent event = objectMapper.readValue(messagePayload, OrderCreatedEvent.class);
        log.info("Consumed OrderCreatedEvent for order ID: {}", event.orderId());
        workflowManager.startSaga(event);
    }
}
