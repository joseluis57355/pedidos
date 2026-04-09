package com.jositoluiso.pedidos.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private String customerName;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
}