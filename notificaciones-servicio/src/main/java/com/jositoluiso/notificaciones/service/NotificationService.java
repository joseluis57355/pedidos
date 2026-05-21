package com.jositoluiso.notificaciones.service;

import com.jositoluiso.notificaciones.entity.Notification;
import com.jositoluiso.notificaciones.event.OrderCreatedEvent;
import com.jositoluiso.notificaciones.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public Notification record(OrderCreatedEvent event) {
        Notification notification = Notification.builder()
                .orderId(event.getOrderId())
                .customerName(event.getCustomerName())
                .amount(event.getAmount())
                .orderCreatedAt(event.getCreatedAt())
                .message(String.format("Order %d created for customer %s", event.getOrderId(), event.getCustomerName()))
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(notification);
    }

    public List<Notification> findAll() {
        return repository.findAll();
    }

    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }
}
