package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        // Validación de datos
        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (order.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        // Guardar el pedido
        return orderRepository.save(order);
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
    }
}
