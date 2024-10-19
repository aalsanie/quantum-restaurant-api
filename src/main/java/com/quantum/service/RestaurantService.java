package com.quantum.service;

import com.quantum.model.Employee;
import com.quantum.model.Restaurant;
import com.quantum.model.RestaurantLayout;
import com.quantum.repository.EmployeeRepository;
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
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantLayoutRepository restaurantLayoutRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             RestaurantLayoutRepository restaurantLayoutRepository,
                             EmployeeRepository employeeRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantLayoutRepository = restaurantLayoutRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Registers a new restaurant with a PENDING status.
     * @param restaurant The restaurant object to register.
     * @return The saved restaurant with a PENDING status.
     */
    @Transactional
    public Restaurant registerRestaurant(Restaurant restaurant) {
        restaurant.setStatus(Restaurant.Status.PENDING);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    /**
     * Approves a restaurant registration, changing its status to APPROVED.
     * @param restaurantId The UUID of the restaurant to approve.
     * @return The approved restaurant.
     * @throws jakarta.persistence.EntityNotFoundException if the restaurant does not exist.
     */
    @Transactional
    public Restaurant approveRestaurant(UUID restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(Restaurant.Status.APPROVED);
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    /**
     * Retrieves a restaurant by its ID.
     * @param restaurantId The UUID of the restaurant.
     * @return The found restaurant.
     * @throws jakarta.persistence.EntityNotFoundException if the restaurant does not exist.
     */
    public Restaurant getRestaurantById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    /**
     * Retrieves a list of all registered restaurants.
     * @return A list of all restaurants.
     */
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Updates restaurant details.
     * @param restaurantId The UUID of the restaurant to update.
     * @param updatedRestaurant The restaurant details to update.
     * @return The updated restaurant.
     * @throws EntityNotFoundException if the restaurant does not exist.
     */
    @Transactional
    public Restaurant updateRestaurant(UUID restaurantId, Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantById(restaurantId);
        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setEmail(updatedRestaurant.getEmail());
        existingRestaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(existingRestaurant);
    }

    /**
     * Deletes a restaurant by its ID.
     * @param restaurantId The UUID of the restaurant to delete.
     * @throws EntityNotFoundException if the restaurant does not exist.
     */
    @Transactional
    public void deleteRestaurant(UUID restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    /**
     * Creates a layout for a specific restaurant.
     * @param restaurantId The UUID of the restaurant.
     * @param layout The layout object to create.
     * @return The created RestaurantLayout.
     * @throws EntityNotFoundException if the restaurant does not exist.
     */
    @Transactional
    public RestaurantLayout createLayout(UUID restaurantId, RestaurantLayout layout) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        layout.setRestaurant(restaurant);
        layout.setCreatedAt(LocalDateTime.now());
        layout.setUpdatedAt(LocalDateTime.now());
        return restaurantLayoutRepository.save(layout);
    }

    /**
     * Retrieves all employees of a specific restaurant.
     * @param restaurantId The UUID of the restaurant.
     * @return A list of employees.
     * @throws EntityNotFoundException if the restaurant does not exist.
     */
    public List<Employee> getEmployeesByRestaurant(UUID restaurantId) {
        getRestaurantById(restaurantId); // Ensure the restaurant exists
        return employeeRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Adds a new employee to a restaurant.
     * @param restaurantId The UUID of the restaurant.
     * @param employee The employee object to add.
     * @return The saved employee.
     * @throws EntityNotFoundException if the restaurant does not exist.
     */
    @Transactional
    public Employee addEmployee(UUID restaurantId, Employee employee) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        employee.setRestaurant(restaurant);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        return employeeRepository.save(employee);
    }
}

