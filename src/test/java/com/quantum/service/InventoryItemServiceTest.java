package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.Restaurant;
import com.quantum.repository.InventoryItemRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryItemServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private InventoryItemService inventoryItemService;

    private UUID restaurantId;
    private UUID itemId;
    private Restaurant restaurant;
    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        inventoryItem = new InventoryItem();
        inventoryItem.setId(itemId);
        inventoryItem.setName("Test Item");
        inventoryItem.setCategory("Food");
        inventoryItem.setQuantity(100.0);
        inventoryItem.setUnit("kg");
        inventoryItem.setReorderLevel(10.0);
        inventoryItem.setPricePerUnit(5.0);
        inventoryItem.setRestaurant(restaurant);
        inventoryItem.setCreatedAt(LocalDateTime.now());
        inventoryItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createInventoryItem_ShouldReturnSavedItem_WhenRestaurantExists() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(inventoryItem);

        // Act
        InventoryItem savedItem = inventoryItemService.createInventoryItem(restaurantId, inventoryItem);

        // Assert
        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getName());
        assertEquals(restaurant, savedItem.getRestaurant());
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    void createInventoryItem_ShouldThrowException_WhenRestaurantNotFound() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                inventoryItemService.createInventoryItem(restaurantId, inventoryItem));
        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void getInventoryItemsByRestaurant_ShouldReturnItems() {
        // Arrange
        List<InventoryItem> items = List.of(inventoryItem);
        when(inventoryItemRepository.findByRestaurantId(restaurantId)).thenReturn(items);

        // Act
        List<InventoryItem> retrievedItems = inventoryItemService.getInventoryItemsByRestaurant(restaurantId);

        // Assert
        assertNotNull(retrievedItems);
        assertEquals(1, retrievedItems.size());
        assertEquals(inventoryItem, retrievedItems.get(0));
        verify(inventoryItemRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getInventoryItemById_ShouldReturnItem_WhenItemExists() {
        // Arrange
        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(inventoryItem));

        // Act
        InventoryItem retrievedItem = inventoryItemService.getInventoryItemById(itemId);

        // Assert
        assertNotNull(retrievedItem);
        assertEquals("Test Item", retrievedItem.getName());
        verify(inventoryItemRepository, times(1)).findById(itemId);
    }

    @Test
    void getInventoryItemById_ShouldThrowException_WhenItemNotFound() {
        // Arrange
        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                inventoryItemService.getInventoryItemById(itemId));
        assertEquals("Inventory item not found with ID: " + itemId, exception.getMessage());
        verify(inventoryItemRepository, times(1)).findById(itemId);
    }

    @Test
    void updateInventoryItem_ShouldUpdateItem_WhenItemExists() {
        // Arrange
        InventoryItem updatedItem = new InventoryItem();
        updatedItem.setName("Updated Item");
        updatedItem.setCategory("Beverage");
        updatedItem.setQuantity(50.0);
        updatedItem.setUnit("liters");
        updatedItem.setReorderLevel(5.0);
        updatedItem.setPricePerUnit(2.5);

        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(inventoryItem));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(updatedItem);

        // Act
        InventoryItem result = inventoryItemService.updateInventoryItem(itemId, updatedItem);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Item", result.getName());
        assertEquals("Beverage", result.getCategory());
        assertEquals(50.0, result.getQuantity());
        assertEquals("liters", result.getUnit());
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }

    @Test
    void updateInventoryItem_ShouldThrowException_WhenItemNotFound() {
        // Arrange
        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        InventoryItem updatedItem = new InventoryItem();
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                inventoryItemService.updateInventoryItem(itemId, updatedItem));
        assertEquals("Inventory item not found with ID: " + itemId, exception.getMessage());
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void deleteInventoryItem_ShouldDeleteItem_WhenItemExists() {
        // Arrange
        doNothing().when(inventoryItemRepository).deleteById(itemId);

        // Act
        inventoryItemService.deleteInventoryItem(itemId);

        // Assert
        verify(inventoryItemRepository, times(1)).deleteById(itemId);
    }
}
