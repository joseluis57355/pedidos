package com.jositoluiso.notificaciones.repository;

import com.jositoluiso.notificaciones.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
