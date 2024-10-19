package com.quantum.controller;

import com.quantum.model.Restaurant;
import com.quantum.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<Restaurant> registerRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.registerRestaurant(restaurant));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Restaurant> approveRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.approveRestaurant(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable UUID id, @RequestBody Restaurant updatedRestaurant) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, updatedRestaurant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}

