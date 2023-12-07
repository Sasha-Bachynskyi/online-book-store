package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.OrderItemDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @AfterMapping
    default void toOrderItemsDto(@MappingTarget OrderDto orderDto, Order order) {
        if (!CollectionUtils.isEmpty(order.getOrderItems())) {
            List<OrderItemDto> orderItemsDto = order.getOrderItems().stream()
                    .map(this::toOrderItemDto)
                    .toList();
            orderDto.setOrderItems(orderItemsDto);
        }
    }

    private OrderItemDto toOrderItemDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setBookId(orderItem.getBook().getId());
        dto.setQuantity(orderItem.getQuantity());
        return dto;
    }
}
