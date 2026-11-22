package com.txnexus.inventory_service.repository;

import com.txnexus.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Optional<Inventory> findByProductIdForUpdate(@Param("productId") String productId);
}
