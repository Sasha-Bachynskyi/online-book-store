package com.onlinebookstore.service.order;

import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderRequestDto;
import com.onlinebookstore.dto.order.OrderUpdateRequestDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.OrderMapper;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.order.OrderRepository;
import com.onlinebookstore.repository.orderitem.OrderItemRepository;
import com.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderDto placeOrder(User user, OrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find shopping "
                        + "cart by userId " + user.getId())
        );
        BigDecimal total = shoppingCart.getCartItems().stream()
                .map(cartItem -> cartItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.COMPLETED);
        order.setTotal(total);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> createOrderItem(cartItem, savedOrder))
                .toList();
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(savedOrderItems);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderDto> getAll(User user) {
        return orderRepository.findAllByUserId(user.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public void updateOrderStatus(Long id, OrderUpdateRequestDto requestDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find order by id " + id)
        );
        order.setStatus(requestDto.getStatus());
        orderRepository.save(order);
    }

    private OrderItem createOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return orderItem;
    }
}
