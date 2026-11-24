package com.txnexus.inventory_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.txnexus.commondto.commands.ReleaseInventoryCommand;
import com.txnexus.commondto.commands.ReserveInventoryCommand;
import com.txnexus.inventory_service.service.InventoryReleaseService;
import com.txnexus.inventory_service.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryCommandConsumer {

    private final InventoryReservationService reservationService;
    private final InventoryReleaseService releaseService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = "inventory.commands", groupId = "inventory-service-group")
    public void consume(String rawMessage) {
        if (rawMessage.contains("ReserveInventoryCommand")) {
            ReserveInventoryCommand cmd = objectMapper.readValue(rawMessage, ReserveInventoryCommand.class);
            reservationService.reserve(cmd);
        } else if (rawMessage.contains("ReleaseInventoryCommand")) {
            ReleaseInventoryCommand cmd = objectMapper.readValue(rawMessage, ReleaseInventoryCommand.class);
            releaseService.release(cmd);
        } else {
            // Fallback JSON node inspection if class name isn't embedded
            try {
                ReserveInventoryCommand cmd = objectMapper.readValue(rawMessage, ReserveInventoryCommand.class);
                reservationService.reserve(cmd);
            } catch (Exception ignored) {
                ReleaseInventoryCommand cmd = objectMapper.readValue(rawMessage, ReleaseInventoryCommand.class);
                releaseService.release(cmd);
            }
        }
    }
}
