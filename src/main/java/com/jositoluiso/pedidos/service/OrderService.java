package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.repository.OrderRepository;
import com.jositoluiso.pedidos.config.MetricsConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MetricsConfig metricsConfig;

    /**
     * Crear una nueva orden
     * Invalida el caché de lista completa después de crear
     */
    @CacheEvict(value = "orders", allEntries = true)
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
    
    /**
     * Obtener todas las órdenes
     * Cachea el resultado por 1 hora (configurable en application.properties)
     * Se invalida cuando se crea o elimina una orden
     */
    @Cacheable("orders")
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * Obtener orden por ID
     * Cachea el resultado individual por 1 hora
     * La key es el ID de la orden para cachear cada orden por separado
     */
    @Cacheable(value = "order", key = "#id")
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Eliminar orden
     * Invalida el caché de lista completa y el caché individual de la orden
     */
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "order", key = "#id")
    })
    public void delete(Long id) {
        orderRepository.deleteById(id);
        metricsConfig.incrementOrdersDeleted();
    }
}
