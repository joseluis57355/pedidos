package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.entity.OrderItem;
import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.enums.OrderStatus;
import com.jositoluiso.pedidos.repository.OrderRepository;
import com.jositoluiso.pedidos.repository.ProductRepository;
import com.jositoluiso.pedidos.mapper.OrderMapper;
import com.jositoluiso.pedidos.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

import com.jositoluiso.pedidos.config.MetricsConfig;
import com.jositoluiso.pedidos.dto.OrderItemRequestDTO;
import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MetricsConfig metricsConfig;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Crear una nueva orden
     * Invalida el caché de lista completa después de crear
     */
    /*@CacheEvict(value = "orders", allEntries = true)
    public Order create(Order order) {
        try {
            order.setCreatedAt(LocalDateTime.now());
            // Validación de datos
            if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
                metricsConfig.incrementOrdersCreationError();
                throw new IllegalArgumentException("Customer name cannot be empty");
            }
            if (order.getAmount() == null || order.getAmount().compareTo(BigDecimal.ZERO) < 0) {
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
    }*/

     /**
     * Crear una nueva orden
     * Invalida el caché de lista completa después de crear
     */
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "products", allEntries = true)
    })
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // Validación de datos básicos
        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            metricsConfig.incrementOrdersCreationError();
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            metricsConfig.incrementOrdersCreationError();
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!product.getActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not active");
            }

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for product: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            order.getItems().add(item);

            product.setStock(product.getStock() - itemRequest.getQuantity());

            totalAmount = totalAmount.add(subtotal);
        }

        order.setAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(savedOrder);
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
     * Restaura stock de los productos del pedido y actualiza cachés
     */
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "order", key = "#id"),
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product", allEntries = true)
    })
    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        orderRepository.delete(order);
        metricsConfig.incrementOrdersDeleted();
    }

}

