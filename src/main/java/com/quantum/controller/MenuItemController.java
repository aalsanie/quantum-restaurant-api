package com.quantum.controller;

import com.quantum.model.MenuItem;
import com.quantum.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/menus/{menuId}")
    public ResponseEntity<MenuItem> createMenuItem(@PathVariable UUID menuId, @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(menuItemService.createMenuItem(menuId, menuItem));
    }

    @GetMapping("/menus/{menuId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByMenu(@PathVariable UUID menuId) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByMenu(menuId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable UUID id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable UUID id, @RequestBody MenuItem updatedMenuItem) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, updatedMenuItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
