package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Restaurant;
import com.quantum.model.RestaurantLayout;
import com.quantum.repository.RestaurantLayoutRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class RestaurantLayoutServiceTest {

    @Mock
    private RestaurantLayoutRepository restaurantLayoutRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantLayoutService restaurantLayoutService;

    private UUID restaurantId;
    private UUID layoutId;
    private Restaurant restaurant;
    private RestaurantLayout layout;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantId = UUID.randomUUID();
        layoutId = UUID.randomUUID();

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        layout = new RestaurantLayout();
        layout.setId(layoutId);
        layout.setRestaurant(restaurant);
        layout.setCreatedAt(LocalDateTime.now());
        layout.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createLayout_ShouldReturnSavedLayout() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantLayoutRepository.save(layout)).thenReturn(layout);

        // Act
        RestaurantLayout savedLayout = restaurantLayoutService.createLayout(restaurantId, layout);

        // Assert
        assertNotNull(savedLayout);
        assertEquals(layoutId, savedLayout.getId());
        assertEquals(restaurantId, savedLayout.getRestaurant().getId());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantLayoutRepository, times(1)).save(layout);
    }

    @Test
    void createLayout_ShouldThrowException_WhenRestaurantNotFound() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            restaurantLayoutService.createLayout(restaurantId, layout);
        });

        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantLayoutRepository, never()).save(any(RestaurantLayout.class));
    }

    @Test
    void getLayoutsByRestaurant_ShouldReturnLayoutList() {
        // Arrange
        when(restaurantLayoutRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(layout));

        // Act
        List<RestaurantLayout> layouts = restaurantLayoutService.getLayoutsByRestaurant(restaurantId);

        // Assert
        assertNotNull(layouts);
        assertEquals(1, layouts.size());
        assertEquals(layoutId, layouts.get(0).getId());
        verify(restaurantLayoutRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getLayoutsByRestaurant_ShouldReturnEmptyList_WhenNoLayoutsExist() {
        // Arrange
        when(restaurantLayoutRepository.findByRestaurantId(restaurantId)).thenReturn(List.of());

        // Act
        List<RestaurantLayout> layouts = restaurantLayoutService.getLayoutsByRestaurant(restaurantId);

        // Assert
        assertNotNull(layouts);
        assertTrue(layouts.isEmpty());
        verify(restaurantLayoutRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getLayoutById_ShouldReturnLayout() {
        // Arrange
        when(restaurantLayoutRepository.findById(layoutId)).thenReturn(Optional.of(layout));

        // Act
        RestaurantLayout foundLayout = restaurantLayoutService.getLayoutById(layoutId);

        // Assert
        assertNotNull(foundLayout);
        assertEquals(layoutId, foundLayout.getId());
        verify(restaurantLayoutRepository, times(1)).findById(layoutId);
    }

    @Test
    void getLayoutById_ShouldThrowException_WhenLayoutNotFound() {
        // Arrange
        when(restaurantLayoutRepository.findById(layoutId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            restaurantLayoutService.getLayoutById(layoutId);
        });

        assertEquals("Layout not found with ID: " + layoutId, exception.getMessage());
        verify(restaurantLayoutRepository, times(1)).findById(layoutId);
    }

    @Test
    void deleteLayout_ShouldDeleteLayout_WhenExists() {
        // Arrange
        doNothing().when(restaurantLayoutRepository).deleteById(layoutId);

        // Act
        restaurantLayoutService.deleteLayout(layoutId);

        // Assert
        verify(restaurantLayoutRepository, times(1)).deleteById(layoutId);
    }
}
