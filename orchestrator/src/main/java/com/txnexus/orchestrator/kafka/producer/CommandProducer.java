package com.txnexus.orchestrator.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommand(String topic, UUID orderId, Object command) {
        log.info("Dispatching command: {} to topic: {} for order: {}", command.getClass().getSimpleName(),topic, orderId);
        kafkaTemplate.send(topic, orderId.toString(), command);
    }
}
