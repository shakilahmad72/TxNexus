package com.txnexus.order_service.repository;

import com.txnexus.order_service.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query(value = "SELECT * FROM outbox WHERE processed = false ORDER BY created_at ASC LIMIT 50 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxMessage> findUnprocessedMessagesForUpdate();
}
