package com.quantum.service;

import com.quantum.model.Menu;
import com.quantum.model.MenuItem;
import com.quantum.repository.MenuItemRepository;
import com.quantum.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    private static final String IMAGE_UPLOAD_DIR = "/var/www/html/images/"; //  this directory path means that any image saved here can be accessed at http://yourdomain.com/images/

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, MenuRepository menuRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public MenuItem createMenuItem(UUID menuId, MenuItem menuItem) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with ID: " + menuId));
        menuItem.setMenu(menu);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getMenuItemsByMenu(UUID menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    public MenuItem getMenuItemById(UUID menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));
    }

    @Transactional
    public MenuItem updateMenuItem(UUID menuItemId, MenuItem updatedMenuItem) {
        MenuItem existingMenuItem = getMenuItemById(menuItemId);
        existingMenuItem.setName(updatedMenuItem.getName());
        existingMenuItem.setDescription(updatedMenuItem.getDescription());
        existingMenuItem.setPrice(updatedMenuItem.getPrice());
        existingMenuItem.setAvailable(updatedMenuItem.isAvailable());
        existingMenuItem.setUpdatedAt(LocalDateTime.now());
        return menuItemRepository.save(existingMenuItem);
    }

    public void deleteMenuItem(UUID menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with ID: " + menuItemId));
        menuItemRepository.delete(menuItem);
    }

    public String uploadImage(UUID menuItemId, MultipartFile file) throws IOException {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File targetFile = new File(IMAGE_UPLOAD_DIR + fileName);
        file.transferTo(targetFile);

        String imageUrl = "/images/" + fileName;  // Adjust this path for your setup
        menuItem.setImageUrl(imageUrl);
        menuItemRepository.save(menuItem);

        return imageUrl;
    }
}
