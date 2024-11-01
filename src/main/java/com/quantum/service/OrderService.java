package com.quantum.service;

import com.quantum.model.*;
import com.quantum.repository.EmployeeRepository;
import com.quantum.repository.OrderRepository;
import com.quantum.repository.RestaurantRepository;
import com.quantum.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final EmployeeRepository employeeRepository;

    private final StockTransactionService stockTransactionService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        TableRepository tableRepository,
                        EmployeeRepository employeeRepository,
                        StockTransactionService stockTransactionService) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.employeeRepository = employeeRepository;
        this.stockTransactionService = stockTransactionService;
    }

    @Transactional
    public Order createOrder(UUID restaurantId, int tableId, UUID waiterId, Order order) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + tableId));
        Employee waiter = employeeRepository.findById(waiterId)
                .orElseThrow(() -> new EntityNotFoundException("Waiter not found with ID: " + waiterId));

        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setWaiter(waiter);
        order.setTotalAmount(0.0);
        order.setPaidAmount(0.0);
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // Adjust inventory based on ordered items
        for (OrderItem orderItem : order.getOrderItems()) {
            adjustInventoryForOrderItem(orderItem);
        }

        return savedOrder;
    }

    private void adjustInventoryForOrderItem(OrderItem orderItem) {
        MenuItem menuItem = orderItem.getMenuItem();
        int orderedQuantity = orderItem.getQuantity();

        for (Ingredient ingredient : menuItem.getIngredients()) {
            UUID inventoryItemId = ingredient.getInventoryItem().getId();
            double quantityUsed = ingredient.getQuantity() * orderedQuantity;

            stockTransactionService.useStock(
                    inventoryItemId,
                    quantityUsed,
                    "Used for order ID: " + orderItem.getOrder().getId() + ", MenuItem: " + menuItem.getName()
            );
        }
    }

    public List<Order> getOrdersByRestaurant(UUID restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, Order.Status status) {
        Order existingOrder = getOrderById(orderId);
        existingOrder.setStatus(status);
        existingOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        orderRepository.deleteById(orderId);
    }
}

