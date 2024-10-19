package com.quantum.service;

import com.quantum.model.Employee;
import com.quantum.model.Restaurant;
import com.quantum.repository.EmployeeRepository;
import com.quantum.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           RestaurantRepository restaurantRepository) {
        this.employeeRepository = employeeRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Employee addEmployee(UUID restaurantId, Employee employee) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        employee.setRestaurant(restaurant);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        return employeeRepository.save(employee);
    }

    public List<Employee> getEmployeesByRestaurant(UUID restaurantId) {
        return employeeRepository.findByRestaurantId(restaurantId);
    }

    public Employee getEmployeeById(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
    }

    @Transactional
    public Employee updateEmployee(UUID employeeId, Employee updatedEmployee) {
        Employee existingEmployee = getEmployeeById(employeeId);
        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setRole(updatedEmployee.getRole());
        existingEmployee.setUpdatedAt(LocalDateTime.now());
        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(UUID employeeId) {
        Employee employee = getEmployeeById(employeeId);
        employeeRepository.delete(employee);
    }
}
