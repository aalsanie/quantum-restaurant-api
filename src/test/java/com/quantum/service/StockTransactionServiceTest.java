package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.StockTransaction;
import com.quantum.repository.InventoryItemRepository;
import com.quantum.repository.StockTransactionRepository;
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
public class StockTransactionServiceTest {

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private StockTransactionService stockTransactionService;

    private UUID inventoryItemId;
    private InventoryItem inventoryItem;
    private StockTransaction stockTransaction;

    @BeforeEach
    void setUp() {
        inventoryItemId = UUID.randomUUID();

        inventoryItem = new InventoryItem();
        inventoryItem.setId(inventoryItemId);
        inventoryItem.setName("Test Item");
        inventoryItem.setQuantity(100.0);
        inventoryItem.setUpdatedAt(LocalDateTime.now());

        stockTransaction = new StockTransaction();
        stockTransaction.setInventoryItem(inventoryItem);
        stockTransaction.setQuantity(10.0);
        stockTransaction.setType("PURCHASE");
        stockTransaction.setDate(LocalDateTime.now());
    }

    @Test
    void addStock_ShouldIncreaseQuantity_WhenItemExists() {
        // Arrange
        double quantityToAdd = 10.0;
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(stockTransactionRepository.save(any(StockTransaction.class))).thenReturn(stockTransaction);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(inventoryItem);

        // Act
        InventoryItem updatedItem = stockTransactionService.addStock(inventoryItemId, quantityToAdd, "Stock addition");

        // Assert
        assertNotNull(updatedItem);
        assertEquals(110.0, updatedItem.getQuantity());
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }

    @Test
    void addStock_ShouldThrowException_WhenItemDoesNotExist() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stockTransactionService.addStock(inventoryItemId, 10.0, "Stock addition"));
        assertEquals("Inventory item not found with ID: " + inventoryItemId, exception.getMessage());
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }

    @Test
    void addStock_ShouldThrowException_WhenQuantityIsInvalid() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stockTransactionService.addStock(inventoryItemId, -10.0, "Invalid stock addition"));
        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void useStock_ShouldDecreaseQuantity_WhenSufficientStockExists() {
        // Arrange
        double quantityToUse = 20.0;
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(stockTransactionRepository.save(any(StockTransaction.class))).thenReturn(stockTransaction);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(inventoryItem);

        // Act
        InventoryItem updatedItem = stockTransactionService.useStock(inventoryItemId, quantityToUse, "Used in order");

        // Assert
        assertNotNull(updatedItem);
        assertEquals(80.0, updatedItem.getQuantity());
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }

    @Test
    void useStock_ShouldThrowException_WhenItemDoesNotExist() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stockTransactionService.useStock(inventoryItemId, 10.0, "Usage"));
        assertEquals("Inventory item not found with ID: " + inventoryItemId, exception.getMessage());
    }

    @Test
    void useStock_ShouldThrowException_WhenQuantityExceedsAvailableStock() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stockTransactionService.useStock(inventoryItemId, 200.0, "Exceeding stock"));
        assertEquals("Quantity must be positive and not exceed available stock.", exception.getMessage());
    }

    @Test
    void adjustStock_ShouldAdjustQuantity_WhenItemExists() {
        // Arrange
        double newQuantity = 150.0;
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(stockTransactionRepository.save(any(StockTransaction.class))).thenReturn(stockTransaction);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(inventoryItem);

        // Act
        InventoryItem adjustedItem = stockTransactionService.adjustStock(inventoryItemId, newQuantity, "Adjustment");

        // Assert
        assertNotNull(adjustedItem);
        assertEquals(150.0, adjustedItem.getQuantity());
        verify(stockTransactionRepository, times(1)).save(any(StockTransaction.class));
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }

    @Test
    void adjustStock_ShouldThrowException_WhenItemDoesNotExist() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stockTransactionService.adjustStock(inventoryItemId, 150.0, "Adjustment"));
        assertEquals("Inventory item not found with ID: " + inventoryItemId, exception.getMessage());
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
    }

    @Test
    void getTransactionsByItem_ShouldReturnTransactions() {
        // Arrange
        List<StockTransaction> transactions = List.of(stockTransaction);
        when(stockTransactionRepository.findByInventoryItemId(inventoryItemId)).thenReturn(transactions);

        // Act
        List<StockTransaction> retrievedTransactions = stockTransactionService.getTransactionsByItem(inventoryItemId);

        // Assert
        assertNotNull(retrievedTransactions);
        assertEquals(1, retrievedTransactions.size());
        assertEquals(stockTransaction, retrievedTransactions.get(0));
        verify(stockTransactionRepository, times(1)).findByInventoryItemId(inventoryItemId);
    }
}
