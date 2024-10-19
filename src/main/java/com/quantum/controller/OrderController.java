package com.quantum.controller;

import com.quantum.model.Order;
import com.quantum.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/restaurants/{restaurantId}/tables/{tableId}/waiters/{waiterId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable UUID restaurantId,
            @PathVariable UUID tableId,
            @PathVariable UUID waiterId,
            @RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(restaurantId, tableId, waiterId, order));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<Order>> getOrdersByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestParam Order.Status status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
