package com.quantum.service;


import com.quantum.model.MenuItem;
import com.quantum.model.Order;
import com.quantum.model.OrderItem;
import com.quantum.repository.MenuItemRepository;
import com.quantum.repository.OrderItemRepository;
import com.quantum.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private UUID orderId;
    private UUID menuItemId;
    private UUID orderItemId;
    private Order order;
    private MenuItem menuItem;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        menuItemId = UUID.randomUUID();
        orderItemId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setTotalAmount(0.0);

        menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setName("Pizza");
        menuItem.setPrice(10.0);

        orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setPrice(menuItem.getPrice());
    }

    @Test
    void addOrderItem_ShouldReturnSavedOrderItem() {
        
        int quantity = 2;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderRepository.save(order)).thenReturn(order);

        
        OrderItem savedOrderItem = orderItemService.addOrderItem(orderId, menuItemId, quantity);

        
        assertNotNull(savedOrderItem);
        assertEquals(orderItemId, savedOrderItem.getId());
        assertEquals(quantity, savedOrderItem.getQuantity());
        assertEquals(menuItem.getPrice(), savedOrderItem.getPrice());
        assertEquals(orderId, savedOrderItem.getOrder().getId());
        assertEquals(menuItemId, savedOrderItem.getMenuItem().getId());
        assertEquals(20.0, order.getTotalAmount()); // 2 * 10.0
        verify(orderRepository, times(1)).findById(orderId);
        verify(menuItemRepository, times(1)).findById(menuItemId);
        verify(orderRepository, times(1)).save(order);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void addOrderItem_ShouldThrowException_WhenOrderNotFound() {
        
        int quantity = 2;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.addOrderItem(orderId, menuItemId, quantity);
        });

        assertEquals("Order not found with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(menuItemRepository, never()).findById(menuItemId);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void addOrderItem_ShouldThrowException_WhenMenuItemNotFound() {
        
        int quantity = 2;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.addOrderItem(orderId, menuItemId, quantity);
        });

        assertEquals("MenuItem not found with ID: " + menuItemId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(menuItemRepository, times(1)).findById(menuItemId);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void getOrderItemsByOrder_ShouldReturnOrderItemList() {
        
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));

        
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(orderId);

        
        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        assertEquals(orderItemId, orderItems.get(0).getId());
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getOrderItemsByOrder_ShouldReturnEmptyList_WhenNoItemsExist() {
        
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());

        
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(orderId);

        
        assertNotNull(orderItems);
        assertTrue(orderItems.isEmpty());
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void deleteOrderItem_ShouldDeleteOrderItem_WhenItemExists() {
        
        doNothing().when(orderItemRepository).deleteById(orderItemId);

        
        orderItemService.deleteOrderItem(orderItemId);

        
        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }

    @Test
    void deleteOrderItem_ShouldNotThrowException_WhenItemDoesNotExist() {
        
        doNothing().when(orderItemRepository).deleteById(orderItemId);

        assertDoesNotThrow(() -> orderItemService.deleteOrderItem(orderItemId));
        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }
}
