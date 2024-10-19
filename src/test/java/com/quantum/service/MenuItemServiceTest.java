package com.quantum.service;

import com.quantum.model.Menu;
import com.quantum.model.MenuItem;
import com.quantum.repository.MenuItemRepository;
import com.quantum.repository.MenuRepository;
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
public class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private MenuItem menuItem;
    private UUID menuItemId;
    private Menu menu;
    private UUID menuId;

    @BeforeEach
    public void setup() {
        menuId = UUID.randomUUID();
        menu = new Menu();
        menu.setId(menuId);
        menu.setName("Dinner Menu");

        menuItemId = UUID.randomUUID();
        menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setName("Pizza");
        menuItem.setDescription("Delicious cheese pizza");
        menuItem.setPrice(9.99);
        menuItem.setAvailable(true);
        menuItem.setMenu(menu);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void shouldCreateMenuItem() {
        // Arrange
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        // Act
        MenuItem savedMenuItem = menuItemService.createMenuItem(menuId, menuItem);

        // Assert
        assertThat(savedMenuItem.getName()).isEqualTo("Pizza");
        assertThat(savedMenuItem.getMenu().getId()).isEqualTo(menuId);
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuItemRepository, times(1)).save(menuItem);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingMenuItemForNonExistentMenu() {
        // Arrange
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuItemService.createMenuItem(menuId, menuItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Menu not found with ID: " + menuId);

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    public void shouldGetMenuItemById() {
        // Arrange
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        // Act
        MenuItem foundMenuItem = menuItemService.getMenuItemById(menuItemId);

        // Assert
        assertThat(foundMenuItem).isNotNull();
        assertThat(foundMenuItem.getId()).isEqualTo(menuItemId);
        assertThat(foundMenuItem.getName()).isEqualTo("Pizza");
        verify(menuItemRepository, times(1)).findById(menuItemId);
    }

    @Test
    public void shouldThrowExceptionWhenMenuItemNotFoundById() {
        // Arrange
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuItemService.getMenuItemById(menuItemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("MenuItem not found with ID: " + menuItemId);

        verify(menuItemRepository, times(1)).findById(menuItemId);
    }

    @Test
    public void shouldGetMenuItemsByMenu() {
        // Arrange
        when(menuItemRepository.findByMenuId(menuId)).thenReturn(List.of(menuItem));

        // Act
        List<MenuItem> menuItems = menuItemService.getMenuItemsByMenu(menuId);

        // Assert
        assertThat(menuItems).isNotEmpty();
        assertThat(menuItems.size()).isEqualTo(1);
        assertThat(menuItems.get(0).getName()).isEqualTo("Pizza");
        verify(menuItemRepository, times(1)).findByMenuId(menuId);
    }

    @Test
    public void shouldUpdateMenuItem() {
        // Arrange
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Updated Pizza");
        updatedMenuItem.setDescription("Delicious pizza with extra toppings");
        updatedMenuItem.setPrice(12.99);
        updatedMenuItem.setAvailable(false);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedMenuItem);

        // Act
        MenuItem result = menuItemService.updateMenuItem(menuItemId, updatedMenuItem);

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Pizza");
        assertThat(result.getDescription()).isEqualTo("Delicious pizza with extra toppings");
        assertThat(result.getPrice()).isEqualTo(12.99);
        assertThat(result.isAvailable()).isFalse();
        verify(menuItemRepository, times(1)).findById(menuItemId);
        verify(menuItemRepository, times(1)).save(menuItem);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistentMenuItem() {
        // Arrange
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Updated Pizza");

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuItemService.updateMenuItem(menuItemId, updatedMenuItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("MenuItem not found with ID: " + menuItemId);

        verify(menuItemRepository, times(1)).findById(menuItemId);
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    public void shouldDeleteMenuItem() {
        // Arrange
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        // Act
        menuItemService.deleteMenuItem(menuItemId);

        // Assert
        verify(menuItemRepository, times(1)).delete(menuItem);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentMenuItem() {
        // Arrange
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuItemService.deleteMenuItem(menuItemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("MenuItem not found with ID: " + menuItemId);

        verify(menuItemRepository, times(1)).findById(menuItemId);
        verify(menuItemRepository, never()).delete(any(MenuItem.class));
    }
}

