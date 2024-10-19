package com.quantum.service;

import com.quantum.model.MenuItem;
import com.quantum.model.Order;
import com.quantum.model.OrderItem;
import com.quantum.repository.MenuItemRepository;
import com.quantum.repository.OrderItemRepository;
import com.quantum.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository,
                            OrderRepository orderRepository,
                            MenuItemRepository menuItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public OrderItem addOrderItem(UUID orderId, UUID menuItemId, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice());

        // Update order total amount
        double totalItemPrice = quantity * menuItem.getPrice();
        order.setTotalAmount(order.getTotalAmount() + totalItemPrice);
        orderRepository.save(order);

        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getOrderItemsByOrder(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional
    public void deleteOrderItem(UUID orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }
}

