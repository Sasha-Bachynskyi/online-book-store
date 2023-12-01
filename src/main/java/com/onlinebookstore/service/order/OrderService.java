package com.onlinebookstore.service.order;

import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderRequestDto;
import com.onlinebookstore.dto.order.OrderUpdateRequestDto;
import com.onlinebookstore.model.User;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(User user, OrderRequestDto requestDto);

    List<OrderDto> getAll(User user);

    void updateOrderStatus(Long id, OrderUpdateRequestDto requestDto);
}
