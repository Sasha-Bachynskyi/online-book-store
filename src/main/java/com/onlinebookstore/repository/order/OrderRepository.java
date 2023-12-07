package com.onlinebookstore.repository.order;

import com.onlinebookstore.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems"})
    List<Order> findAllByUserId(Long userId);
}
