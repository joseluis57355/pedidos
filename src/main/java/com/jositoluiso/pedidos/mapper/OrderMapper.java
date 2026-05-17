package com.jositoluiso.pedidos.mapper;

import com.jositoluiso.pedidos.dto.OrderRequestDTO;
import com.jositoluiso.pedidos.dto.OrderResponseDTO;
import com.jositoluiso.pedidos.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class}, imports = {java.time.LocalDateTime.class, java.math.BigDecimal.class})
public interface OrderMapper {

    @Mapping(target = "amount", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "status", expression = "java(com.jositoluiso.pedidos.enums.OrderStatus.PENDING)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    OrderResponseDTO toResponseDTO(Order order);

    List<OrderResponseDTO> toResponseList(List<Order> orders);
}
