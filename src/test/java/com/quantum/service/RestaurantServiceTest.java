package com.quantum.service;

import com.quantum.model.Restaurant;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    public void setup() {
        restaurantId = UUID.randomUUID();
        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");
        restaurant.setEmail("test@example.com");
        restaurant.setPasswordHash("hashedpassword");
        restaurant.setStatus(Restaurant.Status.PENDING);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void shouldRegisterRestaurantWithPendingStatus() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        Restaurant savedRestaurant = restaurantService.registerRestaurant(restaurant);

        // Assert
        assertThat(savedRestaurant.getStatus()).isEqualTo(Restaurant.Status.PENDING);
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void shouldApproveRestaurant() {
        // Arrange
        restaurant.setStatus(Restaurant.Status.PENDING);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        Restaurant approvedRestaurant = restaurantService.approveRestaurant(restaurantId);

        // Assert
        assertThat(approvedRestaurant.getStatus()).isEqualTo(Restaurant.Status.APPROVED);
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void shouldThrowExceptionWhenApprovingNonExistentRestaurant() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> restaurantService.approveRestaurant(restaurantId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurant not found");

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    public void shouldGetRestaurantById() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act
        Restaurant foundRestaurant = restaurantService.getRestaurantById(restaurantId);

        // Assert
        assertThat(foundRestaurant).isNotNull();
        assertThat(foundRestaurant.getId()).isEqualTo(restaurantId);
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    public void shouldThrowExceptionWhenRestaurantNotFoundById() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> restaurantService.getRestaurantById(restaurantId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurant not found");

        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    public void shouldGetAllRestaurants() {
        // Arrange
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        // Act
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();

        // Assert
        assertThat(restaurants).isNotEmpty();
        assertThat(restaurants.size()).isEqualTo(1);
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    public void shouldUpdateRestaurant() {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setEmail("updated@example.com");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        // Act
        Restaurant result = restaurantService.updateRestaurant(restaurantId, updatedRestaurant);

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Restaurant");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistentRestaurant() {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Restaurant");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> restaurantService.updateRestaurant(restaurantId, updatedRestaurant))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurant not found");

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    public void shouldDeleteRestaurant() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act
        restaurantService.deleteRestaurant(restaurantId);

        // Assert
        verify(restaurantRepository, times(1)).delete(restaurant);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentRestaurant() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> restaurantService.deleteRestaurant(restaurantId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurant not found");

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).delete(any(Restaurant.class));
    }
}

