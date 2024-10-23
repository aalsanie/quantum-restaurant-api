package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.PurchaseOrder;
import com.quantum.model.PurchaseOrderItem;
import com.quantum.model.Restaurant;
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

    @Autowired
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                RestaurantRepository restaurantRepository,
                                InventoryItemRepository inventoryItemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.restaurantRepository = restaurantRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(UUID restaurantId, PurchaseOrder order) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        order.setRestaurant(restaurant);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        for (PurchaseOrderItem item : order.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(item.getInventoryItem().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with ID: " + item.getInventoryItem().getId()));
            item.setPurchaseOrder(order);
            item.setInventoryItem(inventoryItem);
        }

        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getPurchaseOrdersByRestaurant(UUID restaurantId) {
        return purchaseOrderRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(UUID orderId, String status) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with ID: " + orderId));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public void deletePurchaseOrder(UUID orderId) {
        purchaseOrderRepository.deleteById(orderId);
    }
}
