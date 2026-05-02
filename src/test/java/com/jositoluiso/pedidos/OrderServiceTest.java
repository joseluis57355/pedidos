package com.jositoluiso.pedidos;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.repository.OrderRepository;
import com.jositoluiso.pedidos.service.OrderService;
import com.jositoluiso.pedidos.config.MetricsConfig;

// JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Mockito
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    // Mock del repository
    @Mock
    private OrderRepository orderRepository;

    // Mock de métricas
    @Mock
    private MetricsConfig metricsConfig;

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

        // DATOS DE ENTRADA
        Order order = new Order();
        order.setCustomerName("Juan");
        order.setAmount(100.0);
        order.setStatus("CREATED");

        // SIMULAMOS LO QUE DEVUELVE EL REPOSITORY
        Order savedOrder = Order.builder()
                .id(1L)
                .customerName("Juan")
                .amount(100.0)
                .status("CREATED")
                .build();

        // Cuando se llame a save, devuelve este objeto
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        // EJECUTAMOS EL MÉTODO REAL
        Order response = orderService.create(order);

        // VALIDAMOS RESULTADO
        assertNotNull(response);
        assertEquals("Juan", response.getCustomerName());
        assertEquals(100.0, response.getAmount());

        // VERIFICAMOS INTERACCIONES
        verify(orderRepository, times(1))
                .save(any(Order.class));
        
        // VERIFICAMOS QUE SE INCREMENTÓ EL CONTADOR DE ÓRDENES CREADAS
        verify(metricsConfig, times(1)).incrementOrdersCreated();
    }

    @Test
    void shouldThrowExceptionForInvalidOrder() {
        // PREPARAMOS LOS DATOS DE ENTRADA INVÁLIDOS
        Order order = new Order();
        order.setCustomerName(""); // Nombre vacío
        order.setAmount(-10.0); // Monto negativo
        order.setStatus("CREATED");

        // VERIFICAMOS QUE SE LANZA UNA EXCEPCIÓN
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.create(order);
        });
    }

    @Test
    void shouldReturn400ForInvalidOrder() {
        // PREPARAMOS LOS DATOS DE ENTRADA INVÁLIDOS
        Order order = new Order();
        order.setCustomerName(""); // Nombre vacío
        order.setAmount(-10.0); // Monto negativo
        order.setStatus("CREATED");

        // SIMULAMOS LO QUE DEVUELVE EL REPOSITORY (NO SE LLAMA A save)
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new IllegalArgumentException("Customer name cannot be empty"));

        // EJECUTAMOS EL MÉTODO REAL Y CAPTURAMOS LA EXCEPCIÓN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.create(order);
        });

        // VALIDAMOS MENSAJE DE EXCEPCIÓN
        assertEquals("Customer name cannot be empty", exception.getMessage());

        // VERIFICAMOS QUE NO SE LLAMA A save
        verify(orderRepository, never())
                .save(any(Order.class));
    }
}