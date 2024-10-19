package com.quantum.service;

import com.quantum.model.Menu;
import com.quantum.model.Restaurant;
import com.quantum.repository.MenuRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Menu createMenu(UUID restaurantId, Menu menu) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        menu.setRestaurant(restaurant);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        return menuRepository.save(menu);
    }

    public List<Menu> getMenusByRestaurant(UUID restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public Menu getMenuById(UUID menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));
    }

    @Transactional
    public Menu updateMenu(UUID menuId, Menu updatedMenu) {
        Menu existingMenu = getMenuById(menuId);
        existingMenu.setName(updatedMenu.getName());
        existingMenu.setDescription(updatedMenu.getDescription());
        existingMenu.setUpdatedAt(LocalDateTime.now());
        return menuRepository.save(existingMenu);
    }

    @Transactional
    public void deleteMenu(UUID menuId) {
        menuRepository.deleteById(menuId);
    }
}
