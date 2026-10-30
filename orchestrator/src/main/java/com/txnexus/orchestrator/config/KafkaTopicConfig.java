package com.txnexus.orchestrator.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name("order.events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCommandsTopic() {
        return TopicBuilder.name("order.commands")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryCommandsTopic() {
        return TopicBuilder.name("inventory.events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryEventsTopic() {
        return TopicBuilder.name("inventory.events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCommandsTopic() {
        return TopicBuilder.name("payment.commands")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name("payment.events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
