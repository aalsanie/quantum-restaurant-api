package com.quantum.controller;

import com.quantum.model.Employee;
import com.quantum.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Employee> addEmployee(@PathVariable UUID restaurantId, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.addEmployee(restaurantId, employee));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<Employee>> getEmployeesByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(employeeService.getEmployeesByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable UUID id, @RequestBody Employee updatedEmployee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, updatedEmployee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}

