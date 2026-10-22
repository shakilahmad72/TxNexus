package com.txnexus.order_service.kafka.publisher;

import com.txnexus.order_service.entity.OutboxMessage;
import com.txnexus.order_service.repository.OutboxRepository;
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

        for (OutboxMessage message : pendingMessages) {
            try {
                String topic = message.getAggregateType().toLowerCase() + ".events"; // order.events

                // Key = aggregateId (orderId) ensures all events for this order hit the same partition
                kafkaTemplate.send(topic, message.getAggregateId().toString(), message.getPayload()).get();

                message.setProcessed(true);
                log.info("Published outbox message ID: {} to topic: {}", message.getId(), topic);
             } catch (Exception ex) {
                log.error("Failed to publish outbox message ID: {}. Will retry next tick.", message.getId(), ex);
                break; // Stop loop on failure to preserve sequential ordering
            }
        }
    }
}
