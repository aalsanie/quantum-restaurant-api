package com.quantum.controller;

import com.quantum.model.Menu;
import com.quantum.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Menu> createMenu(@PathVariable UUID restaurantId, @RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(restaurantId, menu));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<Menu>> getMenusByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(menuService.getMenusByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuById(@PathVariable UUID id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable UUID id, @RequestBody Menu updatedMenu) {
        return ResponseEntity.ok(menuService.updateMenu(id, updatedMenu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
}

