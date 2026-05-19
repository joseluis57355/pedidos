package com.jositoluiso.notificaciones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private String customerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private List<OrderItemEvent> items;
}
