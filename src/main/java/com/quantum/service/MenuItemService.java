package com.quantum.service;

import com.quantum.model.Menu;
import com.quantum.model.MenuItem;
import com.quantum.repository.MenuItemRepository;
import com.quantum.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, MenuRepository menuRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public MenuItem createMenuItem(UUID menuId, MenuItem menuItem) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));
        menuItem.setMenu(menu);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getMenuItemsByMenu(UUID menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    public MenuItem getMenuItemById(UUID menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));
    }

    @Transactional
    public MenuItem updateMenuItem(UUID menuItemId, MenuItem updatedMenuItem) {
        MenuItem existingMenuItem = getMenuItemById(menuItemId);
        existingMenuItem.setName(updatedMenuItem.getName());
        existingMenuItem.setDescription(updatedMenuItem.getDescription());
        existingMenuItem.setPrice(updatedMenuItem.getPrice());
        existingMenuItem.setAvailable(updatedMenuItem.isAvailable());
        existingMenuItem.setUpdatedAt(LocalDateTime.now());
        return menuItemRepository.save(existingMenuItem);
    }

    public void deleteMenuItem(UUID menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));
        menuItemRepository.delete(menuItem);
    }
}
