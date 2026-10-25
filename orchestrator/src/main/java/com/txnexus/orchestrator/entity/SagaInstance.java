package com.txnexus.orchestrator.entity;

import com.txnexus.commondto.enums.SagaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstance {

    @Id
    private UUID sagaId;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    private String customerId;
    private String productId;
    private Integer quantity;
    private Double totalAmount;

    private String failureReason;

    private Instant createdAt;
    private Instant updateAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updateAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = Instant.now();
    }

}
