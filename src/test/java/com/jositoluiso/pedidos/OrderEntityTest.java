package com.jositoluiso.pedidos;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.entity.OrderItem;
import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    @Test
    void addAndRemoveItem_recalculatesAmountAndSetsOrder() {
        Order order = Order.builder()
                .customerName("Test")
                .status(OrderStatus.PENDING)
                .amount(BigDecimal.ZERO)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Prod")
                .price(BigDecimal.valueOf(10))
                .stock(10)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .build();

        // subtotal calculation
        item.calculateSubtotal();

        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertSame(order, item.getOrder());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(order.getAmount()));

        // remove
        order.removeItem(item);
        assertEquals(0, order.getItems().size());
        assertNull(item.getOrder());
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getAmount()));
    }

    @Test
    void prePersist_setsCreatedAtIfNull() {
        Order order = new Order();
        order.setCustomerName("X");
        order.setAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.PENDING);

        assertNull(order.getCreatedAt());
        order.prePersist();
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
