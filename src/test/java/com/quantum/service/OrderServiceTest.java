package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Employee;
import com.quantum.model.Order;
import com.quantum.model.Restaurant;
import com.quantum.model.Table;
import com.quantum.repository.EmployeeRepository;
import com.quantum.repository.OrderRepository;
import com.quantum.repository.RestaurantRepository;
import com.quantum.repository.TableRepository;
import com.quantum.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private TableRepository tableRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID restaurantId;
    private UUID tableId;
    private UUID waiterId;
    private UUID orderId;
    private Restaurant restaurant;
    private Table table;
    private Employee waiter;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantId = UUID.randomUUID();
        tableId = UUID.randomUUID();
        waiterId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        table = new Table();
        table.setId(tableId);

        waiter = new Employee();
        waiter.setId(waiterId);

        order = new Order();
        order.setId(orderId);
        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setWaiter(waiter);
        order.setTotalAmount(0.0);
        order.setPaidAmount(0.0);
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    void createOrder_ShouldReturnSavedOrder() {
        
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(employeeRepository.findById(waiterId)).thenReturn(Optional.of(waiter));
        when(orderRepository.save(order)).thenReturn(order);

        
        Order savedOrder = orderService.createOrder(restaurantId, tableId, waiterId, order);

        
        assertNotNull(savedOrder);
        assertEquals(orderId, savedOrder.getId());
        assertEquals(Order.Status.PENDING, savedOrder.getStatus());
        assertEquals(restaurantId, savedOrder.getRestaurant().getId());
        assertEquals(tableId, savedOrder.getTable().getId());
        assertEquals(waiterId, savedOrder.getWaiter().getId());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, times(1)).findById(tableId);
        verify(employeeRepository, times(1)).findById(waiterId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void createOrder_ShouldThrowException_WhenRestaurantNotFound() {
        
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

         
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(restaurantId, tableId, waiterId, order);
        });

        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, never()).findById(tableId);
        verify(employeeRepository, never()).findById(waiterId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenTableNotFound() {
        
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

         
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(restaurantId, tableId, waiterId, order);
        });

        assertEquals("Table not found with ID: " + tableId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, times(1)).findById(tableId);
        verify(employeeRepository, never()).findById(waiterId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenWaiterNotFound() {
        
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(employeeRepository.findById(waiterId)).thenReturn(Optional.empty());

         
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(restaurantId, tableId, waiterId, order);
        });

        assertEquals("Waiter not found with ID: " + waiterId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, times(1)).findById(tableId);
        verify(employeeRepository, times(1)).findById(waiterId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersByRestaurant_ShouldReturnOrderList() {
        
        when(orderRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(order));

        
        List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);

        
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(orderId, orders.get(0).getId());
        verify(orderRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        
        Order foundOrder = orderService.getOrderById(orderId);

        
        assertNotNull(foundOrder);
        assertEquals(orderId, foundOrder.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

         
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertEquals("Order not found with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        Order.Status newStatus = Order.Status.COMPLETED;

        
        Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

        
        assertNotNull(updatedOrder);
        assertEquals(newStatus, updatedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void deleteOrder_ShouldDeleteOrder_WhenOrderExists() {
        
        doNothing().when(orderRepository).deleteById(orderId);

        
        orderService.deleteOrder(orderId);

        
        verify(orderRepository, times(1)).deleteById(orderId);
    }
}
