package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.repository.OrderRepository;
import com.jositoluiso.pedidos.config.MetricsConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MetricsConfig metricsConfig;

    public Order create(Order order) {
        try {
            order.setCreatedAt(LocalDateTime.now());
            // Validación de datos
            if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
                metricsConfig.incrementOrdersCreationError();
                throw new IllegalArgumentException("Customer name cannot be empty");
            }
            if (order.getAmount() < 0) {
                metricsConfig.incrementOrdersCreationError();
                throw new IllegalArgumentException("Amount cannot be negative");
            }
            // Guardar el pedido
            Order savedOrder = orderRepository.save(order);
            metricsConfig.incrementOrdersCreated();
            return savedOrder;
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                metricsConfig.incrementOrdersCreationError();
            }
            throw e;
        }
    }
    
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
        metricsConfig.incrementOrdersDeleted();
    }
}
