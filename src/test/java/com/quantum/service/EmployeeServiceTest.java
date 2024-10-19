package com.quantum.service;

import com.quantum.model.Employee;
import com.quantum.model.Restaurant;
import com.quantum.repository.EmployeeRepository;
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
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private UUID employeeId;
    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    public void setup() {
        restaurantId = UUID.randomUUID();
        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        employeeId = UUID.randomUUID();
        employee = new Employee();
        employee.setId(employeeId);
        employee.setName("John Doe");
        employee.setRole(Employee.Role.WAITER);
        employee.setRestaurant(restaurant);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void shouldAddEmployeeToRestaurant() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee savedEmployee = employeeService.addEmployee(restaurantId, employee);

        // Assert
        assertThat(savedEmployee.getName()).isEqualTo("John Doe");
        assertThat(savedEmployee.getRole()).isEqualTo(Employee.Role.WAITER);
        assertThat(savedEmployee.getRestaurant().getId()).isEqualTo(restaurantId);
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void shouldThrowExceptionWhenAddingEmployeeToNonExistentRestaurant() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.addEmployee(restaurantId, employee))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurant not found with ID: " + restaurantId);

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void shouldGetEmployeeById() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Employee foundEmployee = employeeService.getEmployeeById(employeeId);

        // Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getId()).isEqualTo(employeeId);
        assertThat(foundEmployee.getName()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    public void shouldThrowExceptionWhenEmployeeNotFoundById() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    public void shouldGetEmployeesByRestaurant() {
        // Arrange
        when(employeeRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(employee));

        // Act
        List<Employee> employees = employeeService.getEmployeesByRestaurant(restaurantId);

        // Assert
        assertThat(employees).isNotEmpty();
        assertThat(employees.size()).isEqualTo(1);
        assertThat(employees.get(0).getName()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    public void shouldUpdateEmployee() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Jane Doe");
        updatedEmployee.setRole(Employee.Role.MANAGER);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updatedEmployee);

        // Assert
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getRole()).isEqualTo(Employee.Role.MANAGER);
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistentEmployee() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Jane Doe");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployee(employeeId, updatedEmployee))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void shouldDeleteEmployee() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentEmployee() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(employeeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}

