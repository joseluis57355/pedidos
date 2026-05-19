package com.jositoluiso.pedidos.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.jositoluiso.pedidos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

/**
 * Health check personalizado para verificar el estado de la aplicación.
 * 
 * Verifica:
 * - Conexión a base de datos (intentando una consulta)
 * - Estado general de la aplicación
 */
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final OrderRepository orderRepository;

    @Override
    public Health health() {
        try {
            // Intenta obtener el número de órdenes
            long totalOrders = orderRepository.count();
            
            return Health.up()
                    .withDetail("database", "connected")
                    .withDetail("total_orders", totalOrders)
                    .withDetail("status", "Application is running normally")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "disconnected")
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Application has critical issues")
                    .build();
        }
    }
}
