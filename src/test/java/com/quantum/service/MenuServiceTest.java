package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Menu;
import com.quantum.model.Restaurant;
import com.quantum.repository.MenuRepository;
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

class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuService menuService;

    private UUID restaurantId;
    private UUID menuId;
    private Restaurant restaurant;
    private Menu menu;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantId = UUID.randomUUID();
        menuId = UUID.randomUUID();
        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        menu = new Menu();
        menu.setId(menuId);
        menu.setName("Test Menu");
        menu.setDescription("Test Description");
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menu.setRestaurant(restaurant);
    }

    @Test
    void createMenu_ShouldReturnSavedMenu_WhenRestaurantExists() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuRepository.save(menu)).thenReturn(menu);

        // Act
        Menu savedMenu = menuService.createMenu(restaurantId, menu);

        // Assert
        assertNotNull(savedMenu);
        assertEquals(menu.getName(), savedMenu.getName());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void createMenu_ShouldThrowException_WhenRestaurantDoesNotExist() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            menuService.createMenu(restaurantId, menu);
        });
        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(menuRepository, never()).save(menu);
    }

    @Test
    void getMenusByRestaurant_ShouldReturnListOfMenus() {
        // Arrange
        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(menu));

        // Act
        List<Menu> menus = menuService.getMenusByRestaurant(restaurantId);

        // Assert
        assertNotNull(menus);
        assertEquals(1, menus.size());
        assertEquals(menu.getName(), menus.get(0).getName());
        verify(menuRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getMenuById_ShouldReturnMenu_WhenMenuExists() {
        // Arrange
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // Act
        Menu foundMenu = menuService.getMenuById(menuId);

        // Assert
        assertNotNull(foundMenu);
        assertEquals(menu.getName(), foundMenu.getName());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    void getMenuById_ShouldThrowException_WhenMenuDoesNotExist() {
        // Arrange
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            menuService.getMenuById(menuId);
        });
        assertEquals("Menu not found with ID: " + menuId, exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    void updateMenu_ShouldReturnUpdatedMenu_WhenMenuExists() {
        // Arrange
        Menu updatedMenu = new Menu();
        updatedMenu.setName("Updated Name");
        updatedMenu.setDescription("Updated Description");
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        // Act
        Menu result = menuService.updateMenu(menuId, updatedMenu);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void updateMenu_ShouldThrowException_WhenMenuDoesNotExist() {
        // Arrange
        Menu updatedMenu = new Menu();
        updatedMenu.setName("Updated Name");
        updatedMenu.setDescription("Updated Description");
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            menuService.updateMenu(menuId, updatedMenu);
        });
        assertEquals("Menu not found with ID: " + menuId, exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(menu);
    }

    @Test
    void deleteMenu_ShouldCallDelete_WhenMenuExists() {
        // Arrange
        when(menuRepository.existsById(menuId)).thenReturn(true);

        // Act
        menuService.deleteMenu(menuId);

        // Assert
        verify(menuRepository, times(1)).deleteById(menuId);
    }

    @Test
    void deleteMenu_ShouldThrowException_WhenMenuDoesNotExist() {
        // Arrange
        when(menuRepository.existsById(menuId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            menuService.deleteMenu(menuId);
        });
        assertEquals("Menu not found with ID: " + menuId, exception.getMessage());
        verify(menuRepository, never()).deleteById(menuId);
    }
}
