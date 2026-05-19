package com.jositoluiso.pedidos.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEvent {

    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
