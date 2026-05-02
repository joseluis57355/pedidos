package com.jositoluiso.pedidos.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de métricas personalizadas para monitorización.
 * 
 * Proporciona contadores y medidores para rastrear:
 * - Órdenes creadas
 * - Órdenes eliminadas
 * - Errores en creación de órdenes
 */
@Configuration
@Getter
public class MetricsConfig {

    private final Counter ordersCreatedCounter;
    private final Counter ordersDeletedCounter;
    private final Counter ordersCreationErrorCounter;

    public MetricsConfig(MeterRegistry meterRegistry) {
        // Contador de órdenes creadas
        this.ordersCreatedCounter = Counter.builder("orders.created")
                .description("Número total de órdenes creadas")
                .tag("application", "pedidos")
                .register(meterRegistry);

        // Contador de órdenes eliminadas
        this.ordersDeletedCounter = Counter.builder("orders.deleted")
                .description("Número total de órdenes eliminadas")
                .tag("application", "pedidos")
                .register(meterRegistry);

        // Contador de errores en creación
        this.ordersCreationErrorCounter = Counter.builder("orders.creation.errors")
                .description("Número total de errores en creación de órdenes")
                .tag("application", "pedidos")
                .register(meterRegistry);
    }

    /**
     * Incrementa el contador de órdenes creadas.
     * Se llama automáticamente cuando se crea una orden.
     */
    public void incrementOrdersCreated() {
        ordersCreatedCounter.increment();
    }

    /**
     * Incrementa el contador de órdenes eliminadas.
     * Se llama automáticamente cuando se elimina una orden.
     */
    public void incrementOrdersDeleted() {
        ordersDeletedCounter.increment();
    }

    /**
     * Incrementa el contador de errores en creación.
     * Se llama cuando ocurre una excepción al crear una orden.
     */
    public void incrementOrdersCreationError() {
        ordersCreationErrorCounter.increment();
    }
}
