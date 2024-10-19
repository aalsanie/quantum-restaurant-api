package com.quantum.controller;

import com.quantum.model.RestaurantLayout;
import com.quantum.service.RestaurantLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/layouts")
public class RestaurantLayoutController {

    private final RestaurantLayoutService layoutService;

    @Autowired
    public RestaurantLayoutController(RestaurantLayoutService layoutService) {
        this.layoutService = layoutService;
    }

    @PostMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantLayout> createLayout(@PathVariable UUID restaurantId, @RequestBody RestaurantLayout layout) {
        return ResponseEntity.ok(layoutService.createLayout(restaurantId, layout));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<RestaurantLayout>> getLayoutsByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(layoutService.getLayoutsByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantLayout> getLayoutById(@PathVariable UUID id) {
        return ResponseEntity.ok(layoutService.getLayoutById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayout(@PathVariable UUID id) {
        layoutService.deleteLayout(id);
        return ResponseEntity.noContent().build();
    }
}
