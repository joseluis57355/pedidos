package com.jositoluiso.pedidos.controller;

import com.jositoluiso.pedidos.entity.Order;
import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;
import com.jositoluiso.pedidos.service.OrderService;
import com.jositoluiso.pedidos.mapper.OrderMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Orders management API")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Create order", description = "Creates a new order")
    @PostMapping
    public OrderResponseDTO create(@Valid @RequestBody OrderRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public List<OrderResponseDTO> getAll() {
        List<Order> orders = orderService.findAll();
        return orderMapper.toResponseList(orders);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return orderMapper.toResponseDTO(order);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
