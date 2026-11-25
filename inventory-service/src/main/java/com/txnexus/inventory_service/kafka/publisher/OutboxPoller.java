package com.txnexus.inventory_service.kafka.publisher;

import com.txnexus.inventory_service.entity.OutboxMessage;
import com.txnexus.inventory_service.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void pollAndPublish() {
        List<OutboxMessage> pendingMessages = outboxRepository.findUnprocessedMessagesForUpdate();

        for (OutboxMessage msg : pendingMessages) {
            try {
                // Emits to inventory.events
                kafkaTemplate.send("inventory.events", msg.getAggregateId().toString(), msg.getPayload()).get();
                msg.setProcessed(true);
                log.info("Published outbox event {} for order {}", msg.getEventType(), msg.getAggregateId());
            } catch (Exception ex) {
                log.error("Failed to publish inventory outbox id {}. Retrying next tick.", msg.getId(), ex);
                break;
            }
        }
    }
}
