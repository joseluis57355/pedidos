package com.jositoluiso.pedidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jositoluiso.pedidos.dto.OrderItemRequestDTO;
import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;
import com.jositoluiso.pedidos.enums.OrderStatus;
import com.jositoluiso.pedidos.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_endpoint_returnsOk() throws Exception {
        OrderItemRequestDTO item = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(1)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .customerName("Juan")
                .items(List.of(item))
                .build();

        OrderResponseDTO saved = OrderResponseDTO.builder()
                .id(1L)
                .customerName("Juan")
                .amount(BigDecimal.valueOf(10))
                .status(OrderStatus.CREATED)
                .build();

        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
