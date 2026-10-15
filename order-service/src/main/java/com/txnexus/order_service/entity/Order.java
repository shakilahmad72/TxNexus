package com.txnexus.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
