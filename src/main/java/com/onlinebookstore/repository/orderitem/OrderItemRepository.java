package com.onlinebookstore.repository.orderitem;

import com.onlinebookstore.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "book")
    List<OrderItem> findAllByOrderId(Long orderId);

    @EntityGraph(attributePaths = "book")
    Optional<OrderItem> findByOrderIdAndId(Long orderId, Long itemId);
}
