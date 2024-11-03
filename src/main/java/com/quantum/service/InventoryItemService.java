package com.quantum.service;

import com.quantum.model.InventoryItem;
import com.quantum.model.Restaurant;
import com.quantum.repository.InventoryItemRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public InventoryItemService(InventoryItemRepository inventoryItemRepository, RestaurantRepository restaurantRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public InventoryItem createInventoryItem(UUID restaurantId, InventoryItem item) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        item.setRestaurant(restaurant);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return inventoryItemRepository.save(item);
    }

    public List<InventoryItem> getInventoryItemsByRestaurant(UUID restaurantId) {
        return inventoryItemRepository.findByRestaurantId(restaurantId);
    }

    public InventoryItem getInventoryItemById(UUID itemId) {
        return inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with ID: " + itemId));
    }

    @Transactional
    public InventoryItem updateInventoryItem(UUID itemId, InventoryItem updatedItem) {
        InventoryItem existingItem = getInventoryItemById(itemId);
        existingItem.setName(updatedItem.getName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setUnit(updatedItem.getUnit());
        existingItem.setReorderLevel(updatedItem.getReorderLevel());
        existingItem.setPricePerUnit(updatedItem.getPricePerUnit());
        existingItem.setUpdatedAt(LocalDateTime.now());
        return inventoryItemRepository.save(existingItem);
    }

    @Transactional
    public void deleteInventoryItem(UUID itemId) {
        inventoryItemRepository.deleteById(itemId);
    }
}
