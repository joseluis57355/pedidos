package com.jositoluiso.pedidos.repository;

import com.jositoluiso.pedidos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}