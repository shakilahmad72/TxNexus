package com.txnexus.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.txnexus.commondto.enums.OrderStatus;
import com.txnexus.commondto.events.OrderCreatedEvent;
import com.txnexus.order_service.dto.CreateOrderRequest;
import com.txnexus.order_service.dto.OrderResponse;
import com.txnexus.order_service.entity.Order;
import com.txnexus.order_service.entity.OutboxMessage;
import com.txnexus.order_service.repository.OrderRepository;
import com.txnexus.order_service.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public OrderResponse createOrder(CreateOrderRequest request) {
        UUID orderId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();

        // 1. Persist the Domain Entity
        Order order = Order.builder()
                .id(orderId)
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(request.getTotalAmount())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("Order saved with ID: {} and status: PENDING", orderId);

        // 2. Prepare Domain Event from common-dto
        OrderCreatedEvent event = new OrderCreatedEvent(
                sagaId,
                orderId,
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity(),
                request.getTotalAmount()
        );

        // 3. Persist Outbox Record within the SAME database transaction
        OutboxMessage outboxMessage = OutboxMessage.builder()
                .aggregateType("ORDER")
                .aggregateId(orderId)
                .eventType(OrderCreatedEvent.class.getSimpleName())
                .payload(objectMapper.writeValueAsString(event))
                .processed(false)
                .build();

        outboxRepository.save(outboxMessage);
        log.info("Outbox message created for Order ID: {}", orderId);

        return OrderResponse.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
