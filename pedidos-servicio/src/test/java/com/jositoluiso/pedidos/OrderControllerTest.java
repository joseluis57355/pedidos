package com.jositoluiso.pedidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jositoluiso.pedidos.controller.OrderController;
import com.jositoluiso.pedidos.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    // Mock del service 
    @Mock
    private OrderService orderService;

    @Mock
    private com.jositoluiso.pedidos.mapper.OrderMapper orderMapper;

    // Inyectamos el mock en el controller
    @InjectMocks
    private OrderController orderController;

    // MockMvc manual (SIN Spring context)
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateOrder() throws Exception {

        // DATOS DE ENTRADA: use OrderRequestDTO with one item
        com.jositoluiso.pedidos.dto.OrderItemRequestDTO item = com.jositoluiso.pedidos.dto.OrderItemRequestDTO.builder()
            .productId(1L)
            .quantity(1)
            .build();

        com.jositoluiso.pedidos.dto.OrderRequestDTO request = com.jositoluiso.pedidos.dto.OrderRequestDTO.builder()
            .customerName("Juan")
            .items(java.util.List.of(item))
            .build();

        // Resultado simulado del servicio
        com.jositoluiso.pedidos.dto.OrderResponseDTO response = com.jositoluiso.pedidos.dto.OrderResponseDTO.builder()
            .id(1L)
            .customerName("Juan")
            .amount(java.math.BigDecimal.valueOf(100.0))
            //.status("PENDING")
            .build();

        when(orderService.createOrder(any(com.jositoluiso.pedidos.dto.OrderRequestDTO.class)))
                .thenReturn(response);

        // PETICIÓN HTTP
        mockMvc.perform(post("/orders")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // VERIFICACIÓN: el service debe ejecutarse exactamente una vez
        verify(orderService, times(1)).createOrder(any(com.jositoluiso.pedidos.dto.OrderRequestDTO.class));
    }
}