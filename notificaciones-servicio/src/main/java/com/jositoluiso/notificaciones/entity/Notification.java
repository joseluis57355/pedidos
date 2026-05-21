package com.jositoluiso.notificaciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String customerName;

    private BigDecimal amount;

    private LocalDateTime orderCreatedAt;

    private String message;

    private LocalDateTime createdAt;
}
