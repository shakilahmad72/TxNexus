package com.txnexus.orchestrator.kafka.consumer;

import com.txnexus.commondto.events.InventoryReservedEvent;
import com.txnexus.orchestrator.service.SagaWorkflowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final SagaWorkflowManager workflowManager;

    @KafkaListener(topics = "inventory.events", groupId = "saga-orchestrator-group")
    public void consume(InventoryReservedEvent event) {
        workflowManager.onInventoryReserved(event.sagaId());
    }
}
