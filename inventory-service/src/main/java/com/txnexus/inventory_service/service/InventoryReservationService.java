package com.txnexus.inventory_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.txnexus.commondto.commands.ReserveInventoryCommand;
import com.txnexus.commondto.events.InventoryReservationFailedEvent;
import com.txnexus.inventory_service.entity.Inventory;
import com.txnexus.inventory_service.repository.InventoryRepository;
import com.txnexus.inventory_service.repository.OutboxRepository;
import com.txnexus.inventory_service.repository.ProcessedCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final OutboxRepository outboxRepository;
    private final ProcessedCommandRepository processedCommandRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void reserve(ReserveInventoryCommand command) {
        String commandKey = "RESERVE_" + command.sagaId();
        if (processedCommandRepository.existsById(commandKey)) {
            log.warn("Command {} already processed. Skipping.", commandKey);
            return;
        }

        Optional<Inventory> optInventory = inventoryRepository.findByProductIdForUpdate(command.productId());

        if (optInventory.isEmpty() || optInventory.get().getAvailableStock() < command.quantity()) {
            log.warn("Insufficient stock for product {}. Emitting failure event.", command.productId());

            InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                    command.sagaId(),
                    command.orderId(),
                    "Insufficient stock available"
            );

            saveOutbox(command.orderId(), InventoryReservationFailedEvent.class.getSimpleName(), failedEvent);
        } else {
            Inventory inventory = optInventory.get();
            inventory.setAvailableStock(inventory.getAvailableStock() - command.quantity());
            inventory.setReservedStock(inventory.getReservedStock() + command.quantity());
            inventoryRepository.save(inventory);


        }
    }
}
