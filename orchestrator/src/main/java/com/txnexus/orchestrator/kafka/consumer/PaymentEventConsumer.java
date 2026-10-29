package com.txnexus.orchestrator.kafka.consumer;

import com.txnexus.commondto.events.PaymentAuthorizedEvent;
import com.txnexus.commondto.events.PaymentFailedEvent;
import com.txnexus.orchestrator.service.SagaWorkflowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "payment.events", groupId = "saga-orchestrator-group")
public class PaymentEventConsumer {

    private final SagaWorkflowManager workflowManager;

    @KafkaHandler
    public void onAuthorized(PaymentAuthorizedEvent event) {
        workflowManager.onPaymentAuthorized(event.sagaId());
    }

    @KafkaHandler
    public void onFailed(PaymentFailedEvent event) {
        workflowManager.onPaymentFailed(event.sagaId(), event.failureReason());
    }
}
