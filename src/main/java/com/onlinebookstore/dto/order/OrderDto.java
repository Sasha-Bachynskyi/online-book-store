package com.onlinebookstore.dto.order;

import com.onlinebookstore.dto.OrderItemDto;
import com.onlinebookstore.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private Order.Status status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private List<OrderItemDto> orderItems;
}
