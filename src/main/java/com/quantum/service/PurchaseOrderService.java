package com.quantum.service;

import com.quantum.model.PurchaseOrder;
import com.quantum.repository.InventoryItemRepository;
import com.quantum.repository.PurchaseOrderRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockTransactionService stockTransactionService;

    @Autowired
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                RestaurantRepository restaurantRepository,
                                InventoryItemRepository inventoryItemRepository,
                                StockTransactionService stockTransactionService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.restaurantRepository = restaurantRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockTransactionService = stockTransactionService;
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(UUID restaurantId, PurchaseOrder order) {
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        order.setRestaurant(restaurant);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        for (var item : order.getItems()) {
            var inventoryItem = inventoryItemRepository.findById(item.getInventoryItem().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with ID: " + item.getInventoryItem().getId()));
            item.setPurchaseOrder(order);
            item.setInventoryItem(inventoryItem);
        }

        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(UUID orderId, String status) {
        var order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with ID: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if ("RECEIVED".equalsIgnoreCase(status)) {
            // When the order is marked as RECEIVED, update inventory stock levels
            processReceivedPurchaseOrder(order);
        }

        return purchaseOrderRepository.save(order);
    }

    private void processReceivedPurchaseOrder(PurchaseOrder order) {
        for (var item : order.getItems()) {
            var inventoryItem = item.getInventoryItem();
            double quantityToAdd = item.getQuantity();

            // Use the StockTransactionService to add stock and create a transaction record
            stockTransactionService.addStock(inventoryItem.getId(), quantityToAdd,
                    "Received from Purchase Order ID: " + order.getId());
        }
    }

    public List<PurchaseOrder> getPurchaseOrdersByRestaurant(UUID restaurantId) {
        return purchaseOrderRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public void deletePurchaseOrder(UUID orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with ID: " + orderId));
        purchaseOrderRepository.delete(order);
    }
}
