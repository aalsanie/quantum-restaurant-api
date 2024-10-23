package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.StockTransaction;
import com.quantum.repository.InventoryItemRepository;
import com.quantum.repository.StockTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public StockTransactionService(StockTransactionRepository stockTransactionRepository,
                                   InventoryItemRepository inventoryItemRepository) {
        this.stockTransactionRepository = stockTransactionRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    /**
     * Add stock to an inventory item (e.g., a purchase).
     */
    @Transactional
    public InventoryItem addStock(UUID itemId, double quantity, String note) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with ID: " + itemId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        item.setQuantity(item.getQuantity() + quantity);
        item.setUpdatedAt(LocalDateTime.now());

        StockTransaction transaction = new StockTransaction();
        transaction.setInventoryItem(item);
        transaction.setQuantity(quantity);
        transaction.setType("PURCHASE");
        transaction.setDate(LocalDateTime.now());
        transaction.setNote(note);

        stockTransactionRepository.save(transaction);
        return inventoryItemRepository.save(item);
    }

    /**
     * Use stock from an inventory item (e.g., ingredients used in orders).
     */
    @Transactional
    public InventoryItem useStock(UUID itemId, double quantity, String note) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with ID: " + itemId));

        if (quantity <= 0 || quantity > item.getQuantity()) {
            throw new IllegalArgumentException("Quantity must be positive and not exceed available stock.");
        }

        item.setQuantity(item.getQuantity() - quantity);
        item.setUpdatedAt(LocalDateTime.now());

        StockTransaction transaction = new StockTransaction();
        transaction.setInventoryItem(item);
        transaction.setQuantity(-quantity); // Negative to indicate reduction
        transaction.setType("USAGE");
        transaction.setDate(LocalDateTime.now());
        transaction.setNote(note);

        stockTransactionRepository.save(transaction);
        return inventoryItemRepository.save(item);
    }

    /**
     * Retrieve the stock transactions for a given inventory item.
     */
    public List<StockTransaction> getTransactionsByItem(UUID itemId) {
        return stockTransactionRepository.findByInventoryItemId(itemId);
    }
}
