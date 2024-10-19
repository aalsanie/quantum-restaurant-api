package com.quantum.repository;

import com.quantum.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByRestaurantId(UUID restaurantId);
    List<Order> findByTableId(UUID tableId);
    List<Order> findByWaiterId(UUID waiterId);
}
