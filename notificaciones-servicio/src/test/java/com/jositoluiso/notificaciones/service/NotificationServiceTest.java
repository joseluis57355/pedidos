package com.jositoluiso.notificaciones.service;

import com.jositoluiso.notificaciones.entity.Notification;
import com.jositoluiso.notificaciones.event.OrderCreatedEvent;
import com.jositoluiso.notificaciones.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void shouldSaveNotificationForEvent() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .createdAt(LocalDateTime.of(2026, 5, 21, 12, 0))
                .build();

        Notification saved = Notification.builder()
                .id(1L)
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .orderCreatedAt(event.getCreatedAt())
                .message("Order 10 created for customer Cliente Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.save(any(Notification.class))).thenReturn(saved);

        Notification result = service.record(event);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderId()).isEqualTo(10L);
        assertThat(result.getCustomerName()).isEqualTo("Cliente Test");
        assertThat(result.getMessage()).contains("Order 10 created for customer Cliente Test");
        verify(repository, times(1)).save(any(Notification.class));
    }

    @Test
    void shouldReturnAllNotifications() {
        Notification notification = Notification.builder()
                .id(1L)
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .orderCreatedAt(LocalDateTime.of(2026, 5, 21, 12, 0))
                .message("Order 10 created for customer Cliente Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findAll()).thenReturn(List.of(notification));

        List<Notification> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderId()).isEqualTo(10L);
    }

    @Test
    void shouldReturnNotificationById() {
        Notification notification = Notification.builder()
                .id(1L)
                .orderId(10L)
                .customerName("Cliente Test")
                .amount(BigDecimal.valueOf(25.50))
                .orderCreatedAt(LocalDateTime.of(2026, 5, 21, 12, 0))
                .message("Order 10 created for customer Cliente Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }
}
