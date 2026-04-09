package com.jositoluiso.pedidos.controller;

import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;
import com.jositoluiso.pedidos.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static Order toEntity(OrderRequestDTO dto) {
        return Order.builder()
                .customerName(dto.getCustomerName())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static OrderResponseDTO toResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .amount(order.getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    public static List<OrderResponseDTO> toResponseList(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}