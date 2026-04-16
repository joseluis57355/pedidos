package com.jositoluiso.pedidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jositoluiso.pedidos.controller.OrderController;
import com.jositoluiso.pedidos.entity.Order;
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

        // DATOS DE ENTRADA
        Order order = new Order();
        order.setCustomerName("Juan");
        order.setAmount(100.0);
        order.setStatus("CREATED");

        // MOCK DEL SERVICE
        when(orderService.create(any(Order.class))).thenReturn(order);

        // PETICIÓN HTTP
        mockMvc.perform(post("/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());

        // VERIFICACIÓN
        verify(orderService, times(1)).create(any(Order.class));
    }
    
    @Test
    void shouldReturn400WhenInvalidInput() throws Exception {

        // JSON inválido (faltan campos y amount negativo)
        String invalidJson = """
        {
            "customerName": "",
            "amount": -10,
            "status": ""
        }
        """;

        mockMvc.perform(post("/orders")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // 🔹 IMPORTANTE: el service NO debe ejecutarse
        verify(orderService, times(0)).create(any());
    }
}