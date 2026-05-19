package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.event.OrderCreatedEvent;
import com.jositoluiso.pedidos.event.OrderItemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private static final String TOPIC = "order.created";
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .customerName(order.getCustomerName())
                .amount(order.getAmount())
                .createdAt(order.getCreatedAt())
                .items(buildItems(order))
                .build();

        kafkaTemplate.send(TOPIC, String.valueOf(order.getId()), event);
        log.info("Published Kafka event order.created for orderId={}", order.getId());
    }

    private List<OrderItemEvent> buildItems(Order order) {
        return order.getItems().stream()
                .map(item -> OrderItemEvent.builder()
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
