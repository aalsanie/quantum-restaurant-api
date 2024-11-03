package com.quantum.controller;

import com.quantum.model.InventoryItem;
import com.quantum.model.StockTransaction;
import com.quantum.service.InventoryItemService;
import com.quantum.service.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryItemService inventoryItemService;

    private final StockTransactionService stockTransactionService;

    @Autowired
    public InventoryController(InventoryItemService inventoryItemService,
                               StockTransactionService stockTransactionService) {
        this.inventoryItemService = inventoryItemService;
        this.stockTransactionService = stockTransactionService;
    }

    @PostMapping("/{restaurantId}")
    public ResponseEntity<InventoryItem> createInventoryItem(
            @PathVariable UUID restaurantId,
            @RequestBody InventoryItem item) {
        InventoryItem createdItem = inventoryItemService.createInventoryItem(restaurantId, item);
        return ResponseEntity.ok(createdItem);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<InventoryItem>> getInventoryItemsByRestaurant(@PathVariable UUID restaurantId) {
        List<InventoryItem> items = inventoryItemService.getInventoryItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable UUID itemId) {
        InventoryItem item = inventoryItemService.getInventoryItemById(itemId);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<InventoryItem> updateInventoryItem(
            @PathVariable UUID itemId,
            @RequestBody InventoryItem updatedItem) {
        InventoryItem item = inventoryItemService.updateInventoryItem(itemId, updatedItem);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable UUID itemId) {
        inventoryItemService.deleteInventoryItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/add")
    public ResponseEntity<InventoryItem> addStock(
            @PathVariable UUID itemId,
            @RequestParam double quantity,
            @RequestParam(required = false) String note) {
        InventoryItem updatedItem = stockTransactionService.addStock(itemId, quantity, note);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/{itemId}/use")
    public ResponseEntity<InventoryItem> useStock(
            @PathVariable UUID itemId,
            @RequestParam double quantity,
            @RequestParam(required = false) String note) {
        InventoryItem updatedItem = stockTransactionService.useStock(itemId, quantity, note);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}/transactions")
    public ResponseEntity<List<StockTransaction>> getTransactionsByItem(@PathVariable UUID itemId) {
        List<StockTransaction> transactions = stockTransactionService.getTransactionsByItem(itemId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{itemId}/adjust")
    public ResponseEntity<InventoryItem> adjustStock(
            @PathVariable UUID itemId,
            @RequestParam double newQuantity,
            @RequestParam(required = false) String note) {
        InventoryItem adjustedItem = stockTransactionService.adjustStock(itemId, newQuantity, note);
        return ResponseEntity.ok(adjustedItem);
    }
}
