package com.quantum.controller;

import com.quantum.model.OrderItem;
import com.quantum.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/orders/{orderId}/menu-items/{menuItemId}")
    public ResponseEntity<OrderItem> addOrderItem(
            @PathVariable UUID orderId,
            @PathVariable UUID menuItemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(orderItemService.addOrderItem(orderId, menuItemId, quantity));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrder(orderId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable UUID id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
