package com.txnexus.inventory_service.repository;

import com.txnexus.inventory_service.entity.ProcessedCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedCommandRepository extends JpaRepository<ProcessedCommand, String> {
}
