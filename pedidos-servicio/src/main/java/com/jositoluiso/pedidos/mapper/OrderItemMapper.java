package com.jositoluiso.pedidos.mapper;

import com.jositoluiso.pedidos.dto.OrderItemResponseDTO;
import com.jositoluiso.pedidos.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", expression = "java(item.getProduct() != null ? item.getProduct().getId() : null)")
    @Mapping(target = "productName", expression = "java(item.getProduct() != null ? item.getProduct().getName() : null)")
    OrderItemResponseDTO toResponseDTO(OrderItem item);

}
