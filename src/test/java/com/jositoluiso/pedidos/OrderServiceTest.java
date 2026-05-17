package com.jositoluiso.pedidos;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.entity.OrderItem;
import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.repository.OrderRepository;
import com.jositoluiso.pedidos.repository.ProductRepository;
import com.jositoluiso.pedidos.service.OrderService;
import com.jositoluiso.pedidos.config.MetricsConfig;
import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderItemRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;
import com.jositoluiso.pedidos.mapper.OrderMapper;
import com.jositoluiso.pedidos.enums.OrderStatus;

// JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Mockito
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    // Mock del repository
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    // Mock de métricas
    @Mock
    private MetricsConfig metricsConfig;

    @Mock
    private OrderMapper orderMapper;

    // Inyectamos los mocks en el servicio
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {

        // CREAR UN PRODUCTO MOCKEADO
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(50.0))
                .stock(100)
                .active(true)
                .build();

        // PREPARAR EL ITEM DE LA ORDEN
        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        // PREPARAR LA ORDEN
        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .customerName("Juan")
                .items(java.util.List.of(itemRequest))
                .build();

        // PREPARAR LA ORDEN GUARDADA
        Order savedOrder = Order.builder()
                .id(1L)
                .customerName("Juan")
                .amount(BigDecimal.valueOf(100.0))
                .status(OrderStatus.PENDING)
                .build();

        // PREPARAR LA RESPUESTA MOCKEADA
        OrderResponseDTO expectedResponse = OrderResponseDTO.builder()
                .id(1L)
                .customerName("Juan")
                .amount(BigDecimal.valueOf(100.0))
                .status(OrderStatus.PENDING)
                .build();

        // SIMULAR EL COMPORTAMIENTO DE LOS REPOSITORIES
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(expectedResponse);

        // EJECUTAR EL MÉTODO
        OrderResponseDTO response = orderService.createOrder(orderRequest);

        // VALIDACIONES
        assertNotNull(response);
        assertEquals("Juan", response.getCustomerName());
        assertEquals(OrderStatus.PENDING, response.getStatus());

        // VERIFICAR LAS INTERACCIONES
        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toResponseDTO(any(Order.class));
    }

    @Test
    void shouldThrowExceptionForInvalidOrder() {
        // PREPARAMOS LOS DATOS DE ENTRADA INVÁLIDOS
        OrderRequestDTO order = new OrderRequestDTO();
        order.setCustomerName(""); // Nombre vacío
        order.setItems(java.util.List.of()); // Items vacíos

        // VERIFICAMOS QUE SE LANZA UNA EXCEPCIÓN
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(order);
        });
    }

    @Test
    void shouldReturn400ForInvalidOrder() {
        // PREPARAMOS LOS DATOS DE ENTRADA INVÁLIDOS
        OrderRequestDTO order = new OrderRequestDTO();
        order.setCustomerName(""); // Nombre vacío
        order.setItems(java.util.List.of()); // Items vacíos

        // EJECUTAMOS EL MÉTODO REAL Y CAPTURAMOS LA EXCEPCIÓN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(order);
        });

        // VALIDAMOS MENSAJE DE EXCEPCIÓN
        assertEquals("Customer name cannot be empty", exception.getMessage());

        // VERIFICAMOS QUE NO SE LLAMA A save
        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldRestoreStockWhenDeletingOrder() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(50.0))
                .stock(10)
                .active(true)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50.0))
                .subtotal(BigDecimal.valueOf(100.0))
                .build();

        Order order = Order.builder()
                .id(1L)
                .customerName("Juan")
                .amount(BigDecimal.valueOf(100.0))
                .status(OrderStatus.PENDING)
                .items(java.util.List.of(item))
                .build();

        item.setOrder(order);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.delete(1L);

        assertEquals(12, product.getStock());
        verify(productRepository, times(1)).save(product);
        verify(orderRepository, times(1)).delete(order);
        verify(metricsConfig, times(1)).incrementOrdersDeleted();
    }
}