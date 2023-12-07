package com.onlinebookstore.service.orderitem;

import com.onlinebookstore.dto.OrderItemDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.OrderItemMapper;
import com.onlinebookstore.repository.orderitem.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> getAllByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemById(Long orderId, Long itemId) {
        return orderItemMapper.toDto(orderItemRepository
                .findByOrderIdAndId(orderId, itemId).orElseThrow(
                        () -> new EntityNotFoundException("Couldn't find order "
                                + "item by id " + itemId)
        ));
    }
}
