package com.onlinebookstore.controller;

import com.onlinebookstore.dto.OrderItemDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderRequestDto;
import com.onlinebookstore.dto.order.OrderUpdateRequestDto;
import com.onlinebookstore.model.User;
import com.onlinebookstore.service.order.OrderService;
import com.onlinebookstore.service.orderitem.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Operation(summary = "Place an order",
            description = "Place an order from the user's shopping cart")
    @PostMapping
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid OrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user, requestDto);
    }

    @Operation(summary = "Get all orders", description = "Get all user's orders")
    @GetMapping
    public List<OrderDto> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAll(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order's status", description = "Update order's status")
    @PatchMapping("/{orderId}")
    public void updateOrderStatus(@PathVariable Long orderId,
                                  @RequestBody @Valid OrderUpdateRequestDto requestDto) {
        orderService.updateOrderStatus(orderId, requestDto);
    }

    @Operation(summary = "Get all order items", description = "Get all order items by order id")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllOrderItems(@PathVariable Long orderId) {
        return orderItemService.getAllByOrderId(orderId);
    }

    @Operation(summary = "Get order item",
            description = "Get order item by order id and order item id")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getItemById(orderId, itemId);
    }
}
