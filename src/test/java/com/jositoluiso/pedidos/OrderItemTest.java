package com.jositoluiso.pedidos;

import com.jositoluiso.pedidos.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void calculateSubtotal_setsCorrectValue() {
        OrderItem item = OrderItem.builder()
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();

        // invoke lifecycle method (same logic as @PrePersist/@PreUpdate)
        item.calculateSubtotal();

        assertNotNull(item.getSubtotal());
        assertEquals(0, BigDecimal.valueOf(19.99).multiply(BigDecimal.valueOf(3)).compareTo(item.getSubtotal()));
    }
}
