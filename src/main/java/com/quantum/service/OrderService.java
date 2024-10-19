package com.quantum.service;

import com.quantum.model.Employee;
import com.quantum.model.Order;
import com.quantum.model.Restaurant;
import com.quantum.model.Table;
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

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        TableRepository tableRepository,
                        EmployeeRepository employeeRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Order createOrder(UUID restaurantId, UUID tableId, UUID waiterId, Order order) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + tableId));
        Employee waiter = employeeRepository.findById(waiterId)
                .orElseThrow(() -> new EntityNotFoundException("Waiter not found with ID: " + waiterId));

        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setWaiter(waiter);
        order.setTotalAmount(0.0); // Initial amount is zero, to be updated as items are added
        order.setPaidAmount(0.0);
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
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

