package com.jositoluiso.pedidos.repository;

import com.jositoluiso.pedidos.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}