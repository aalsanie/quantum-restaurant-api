package com.quantum.service;

import com.quantum.model.Floor;
import com.quantum.model.Table;
import com.quantum.repository.FloorRepository;
import com.quantum.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final FloorRepository floorRepository;

    @Autowired
    public TableService(TableRepository tableRepository,
                        FloorRepository floorRepository) {
        this.tableRepository = tableRepository;
        this.floorRepository = floorRepository;
    }

    @Transactional
    public Table createTable(UUID floorId, Table table) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with ID: " + floorId));
        table.setFloor(floor);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(table);
    }

    public List<Table> getTablesByFloor(UUID floorId) {
        return tableRepository.findByFloorId(floorId);
    }

    public Table getTableById(int tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + tableId));
    }

    @Transactional
    public Table updateTable(int tableId, Table updatedTable) {
        Table existingTable = getTableById(tableId);
        existingTable.setTableNumber(updatedTable.getTableNumber());
        existingTable.setCapacity(updatedTable.getCapacity());
        existingTable.setStatus(updatedTable.getStatus());
        existingTable.setTableType(updatedTable.getTableType());
        existingTable.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(existingTable);
    }

    @Transactional
    public void deleteTable(int tableId) {
        tableRepository.deleteById(tableId);
    }
}
