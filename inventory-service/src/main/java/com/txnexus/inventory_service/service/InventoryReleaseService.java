package com.txnexus.inventory_service.service;

import com.txnexus.commondto.commands.ReleaseInventoryCommand;
import com.txnexus.inventory_service.entity.Inventory;
import com.txnexus.inventory_service.entity.ProcessedCommand;
import com.txnexus.inventory_service.repository.InventoryRepository;
import com.txnexus.inventory_service.repository.ProcessedCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReleaseService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedCommandRepository processedCommandRepository;

    public void release(ReleaseInventoryCommand command) {
        String commandKey = "RELEASE_" + command.sagaId();
        if (processedCommandRepository.existsById(commandKey)) {
            log.warn("Release compensation {} already applied. Skipping duplicate.", commandKey);
            return;
        }

        Optional<Inventory> optInventory = inventoryRepository.findByProductIdForUpdate(command.productId());
        if (optInventory.isPresent()) {
            Inventory inventory = optInventory.get();
            int restoreQty = Math.min(inventory.getReservedStock(), command.quantity());

            inventory.setReservedStock(inventory.getReservedStock() - restoreQty);
            inventory.setAvailableStock(inventory.getAvailableStock() + restoreQty);
            inventoryRepository.save(inventory);

            log.info("Compensating action: Restored {} units of product {} back to available stock",
                    restoreQty, command.productId());
        }
        processedCommandRepository.save(new ProcessedCommand(commandKey, Instant.now()));

    }
}
