package com.jositoluiso.notificaciones.listener;

import com.jositoluiso.notificaciones.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedEventListener {

    @KafkaListener(topics = "order.created", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(OrderCreatedEvent event) {
        log.info("Received Kafka event order.created for orderId={}", event.getOrderId());
        log.info("Order details: customer={}, amount={}, createdAt={}", event.getCustomerName(), event.getAmount(), event.getCreatedAt());
        log.info("Simulating notification send for order {} of customer {}", event.getOrderId(), event.getCustomerName());
    }
}
