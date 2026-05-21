package com.jositoluiso.notificaciones.controller;

import com.jositoluiso.notificaciones.entity.Notification;
import com.jositoluiso.notificaciones.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    @Test
    void shouldReturnAllNotifications() throws Exception {
        Notification notification = Notification.builder()
                .id(1L)
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .orderCreatedAt(LocalDateTime.of(2026, 5, 21, 12, 0))
                .message("Order 10 created for customer Cliente Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(service.findAll()).thenReturn(List.of(notification));

        mockMvc.perform(get("/notifications").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderId").value(10))
                .andExpect(jsonPath("$[0].customerName").value("Cliente Test"));
    }

    @Test
    void shouldReturnNotificationById() throws Exception {
        Notification notification = Notification.builder()
                .id(1L)
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .orderCreatedAt(LocalDateTime.of(2026, 5, 21, 12, 0))
                .message("Order 10 created for customer Cliente Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(service.findById(1L)).thenReturn(Optional.of(notification));

        mockMvc.perform(get("/notifications/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.customerName").value("Cliente Test"));
    }

    @Test
    void shouldReturnNotFoundWhenNotificationMissing() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notifications/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
