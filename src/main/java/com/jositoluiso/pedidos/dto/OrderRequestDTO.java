package com.jositoluiso.pedidos.dto;

import java.util.List;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotEmpty(message = "Items list cannot be empty")
    private List<OrderItemRequestDTO> items;
}