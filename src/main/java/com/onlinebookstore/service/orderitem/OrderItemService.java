package com.onlinebookstore.service.orderitem;

import com.onlinebookstore.dto.OrderItemDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getAllByOrderId(Long orderId);

    OrderItemDto getItemById(Long orderId, Long itemId);
}
