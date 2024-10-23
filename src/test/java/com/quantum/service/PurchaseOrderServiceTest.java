package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.PurchaseOrder;
import com.quantum.model.PurchaseOrderItem;
import com.quantum.model.Restaurant;
import com.quantum.repository.InventoryItemRepository;
import com.quantum.repository.PurchaseOrderRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private StockTransactionService stockTransactionService;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private UUID restaurantId;
    private UUID orderId;
    private UUID inventoryItemId;
    private Restaurant restaurant;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderItem purchaseOrderItem;
    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        inventoryItemId = UUID.randomUUID();

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(orderId);
        purchaseOrder.setStatus("PENDING");
        purchaseOrder.setCreatedAt(LocalDateTime.now());
        purchaseOrder.setUpdatedAt(LocalDateTime.now());
        purchaseOrder.setRestaurant(restaurant);

        inventoryItem = new InventoryItem();
        inventoryItem.setId(inventoryItemId);
        inventoryItem.setName("Test Item");
        inventoryItem.setQuantity(100.0);

        purchaseOrderItem = new PurchaseOrderItem();
        purchaseOrderItem.setQuantity(10.0);
        purchaseOrderItem.setInventoryItem(inventoryItem);
        purchaseOrder.setItems(List.of(purchaseOrderItem));
    }

    @Test
    void createPurchaseOrder_ShouldReturnSavedOrder() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

        // Act
        PurchaseOrder savedOrder = purchaseOrderService.createPurchaseOrder(restaurantId, purchaseOrder);

        // Assert
        assertNotNull(savedOrder);
        assertEquals("PENDING", savedOrder.getStatus());
        assertEquals(restaurant, savedOrder.getRestaurant());
        assertEquals(1, savedOrder.getItems().size());
        verify(purchaseOrderRepository, times(1)).save(purchaseOrder);
        verify(inventoryItemRepository, times(1)).findById(inventoryItemId);
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    void createPurchaseOrder_ShouldThrowException_WhenRestaurantNotFound() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                purchaseOrderService.createPurchaseOrder(restaurantId, purchaseOrder));
        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

    @Test
    void updatePurchaseOrderStatus_ShouldUpdateStatus_WhenOrderIsFound() {
        // Arrange
        when(purchaseOrderRepository.findById(orderId)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

        // Act
        PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(orderId, "RECEIVED");

        // Assert
        assertNotNull(updatedOrder);
        assertEquals("RECEIVED", updatedOrder.getStatus());
        verify(purchaseOrderRepository, times(1)).save(purchaseOrder);
        verify(stockTransactionService, times(1))
                .addStock(inventoryItemId, purchaseOrderItem.getQuantity(), "Received from Purchase Order ID: " + orderId);
    }

    @Test
    void updatePurchaseOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(purchaseOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                purchaseOrderService.updatePurchaseOrderStatus(orderId, "RECEIVED"));
        assertEquals("Purchase order not found with ID: " + orderId, exception.getMessage());
        verify(purchaseOrderRepository, times(1)).findById(orderId);
    }


    @Test
    void deletePurchaseOrder_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(purchaseOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                purchaseOrderService.deletePurchaseOrder(orderId));
        assertEquals("Purchase order not found with ID: " + orderId, exception.getMessage());
        verify(purchaseOrderRepository, never()).deleteById(orderId);
    }

    @Test
    void getPurchaseOrdersByRestaurant_ShouldReturnOrders() {
        // Arrange
        List<PurchaseOrder> orders = List.of(purchaseOrder);
        when(purchaseOrderRepository.findByRestaurantId(restaurantId)).thenReturn(orders);

        // Act
        List<PurchaseOrder> retrievedOrders = purchaseOrderService.getPurchaseOrdersByRestaurant(restaurantId);

        // Assert
        assertNotNull(retrievedOrders);
        assertEquals(1, retrievedOrders.size());
        assertEquals(purchaseOrder, retrievedOrders.get(0));
        verify(purchaseOrderRepository, times(1)).findByRestaurantId(restaurantId);
    }
}

