package com.quantum.service;

import com.quantum.model.Restaurant;
import com.quantum.model.RestaurantLayout;
import com.quantum.repository.RestaurantLayoutRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RestaurantLayoutService {

    private final RestaurantLayoutRepository restaurantLayoutRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantLayoutService(RestaurantLayoutRepository restaurantLayoutRepository,
                                   RestaurantRepository restaurantRepository) {
        this.restaurantLayoutRepository = restaurantLayoutRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public RestaurantLayout createLayout(UUID restaurantId, RestaurantLayout layout) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        layout.setRestaurant(restaurant);
        layout.setCreatedAt(LocalDateTime.now());
        layout.setUpdatedAt(LocalDateTime.now());
        return restaurantLayoutRepository.save(layout);
    }

    public List<RestaurantLayout> getLayoutsByRestaurant(UUID restaurantId) {
        return restaurantLayoutRepository.findByRestaurantId(restaurantId);
    }

    public RestaurantLayout getLayoutById(UUID layoutId) {
        return restaurantLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new EntityNotFoundException("Layout not found with ID: " + layoutId));
    }

    @Transactional
    public void deleteLayout(UUID layoutId) {
        restaurantLayoutRepository.deleteById(layoutId);
    }
}

