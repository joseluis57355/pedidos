package com.jositoluiso.pedidos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    private Long productId;

    private Integer quantity;
}