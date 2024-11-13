package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Floor;
import com.quantum.model.RestaurantLayout;
import com.quantum.model.Table;
import com.quantum.repository.FloorRepository;
import com.quantum.repository.RestaurantLayoutRepository;
import com.quantum.repository.TableRepository;
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

class TableServiceTest {

    @Mock
    private TableRepository tableRepository;

    @Mock
    private FloorRepository floorRepository;

    @InjectMocks
    private TableService tableService;

    private UUID floorId;
    private int tableId;
    private Floor floor;
    private Table table;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        floorId = UUID.randomUUID();
        tableId = 1;

        floor = new Floor();
        floor.setId(floorId);
        floor.setCreatedAt(LocalDateTime.now());
        floor.setUpdatedAt(LocalDateTime.now());

        table = new Table();
        table.setId(tableId);
        table.setFloor(floor);
        table.setTableNumber(1);
        table.setCapacity(4);
        table.setStatus(Table.Status.AVAILABLE);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createTable_ShouldReturnSavedTable() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
        when(tableRepository.save(table)).thenReturn(table);

        // Act
        Table savedTable = tableService.createTable(floorId, table);

        // Assert
        assertNotNull(savedTable);
        assertEquals(tableId, savedTable.getId());
        assertEquals(floorId, savedTable.getFloor().getId());
        assertEquals(1, savedTable.getTableNumber());
        verify(floorRepository, times(1)).findById(floorId);
        verify(tableRepository, times(1)).save(table);
    }

    @Test
    void createTable_ShouldThrowException_WhenLayoutNotFound() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tableService.createTable(floorId, table);
        });

        assertEquals("Floor not found with ID: " + floorId, exception.getMessage());
        verify(floorRepository, times(1)).findById(floorId);
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    void getTablesByLayout_ShouldReturnTableList() {
        // Arrange
        when(tableRepository.findByFloorId(floorId)).thenReturn(List.of(table));

        // Act
        List<Table> tables = tableService.getTablesByFloor(floorId);

        // Assert
        assertNotNull(tables);
        assertEquals(1, tables.size());
        assertEquals(tableId, tables.get(0).getId());
        verify(tableRepository, times(1)).findByFloorId(floorId);
    }

    @Test
    void getTablesByLayout_ShouldReturnEmptyList_WhenNoTablesExist() {
        // Arrange
        when(tableRepository.findByFloorId(floorId)).thenReturn(List.of());

        // Act
        List<Table> tables = tableService.getTablesByFloor(floorId);

        // Assert
        assertNotNull(tables);
        assertTrue(tables.isEmpty());
        verify(tableRepository, times(1)).findByFloorId(floorId);
    }

    @Test
    void getTableById_ShouldReturnTable() {
        // Arrange
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));

        // Act
        Table foundTable = tableService.getTableById(tableId);

        // Assert
        assertNotNull(foundTable);
        assertEquals(tableId, foundTable.getId());
        verify(tableRepository, times(1)).findById(tableId);
    }

    @Test
    void getTableById_ShouldThrowException_WhenTableNotFound() {
        // Arrange
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tableService.getTableById(tableId);
        });

        assertEquals("Table not found with ID: " + tableId, exception.getMessage());
        verify(tableRepository, times(1)).findById(tableId);
    }

    @Test
    void updateTable_ShouldReturnUpdatedTable() {
        // Arrange
        Table updatedTable = new Table();
        updatedTable.setTableNumber(2);
        updatedTable.setCapacity(6);
        updatedTable.setStatus(Table.Status.OCCUPIED);

        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(tableRepository.save(table)).thenReturn(table);

        // Act
        Table result = tableService.updateTable(tableId, updatedTable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTableNumber());
        assertEquals(6, result.getCapacity());
        assertEquals(Table.Status.OCCUPIED, result.getStatus());
        verify(tableRepository, times(1)).findById(tableId);
        verify(tableRepository, times(1)).save(table);
    }

    @Test
    void deleteTable_ShouldDeleteTable_WhenExists() {
        // Arrange
        doNothing().when(tableRepository).deleteById(tableId);

        // Act
        tableService.deleteTable(tableId);

        // Assert
        verify(tableRepository, times(1)).deleteById(tableId);
    }
}
