package com.quantum.service;
import com.quantum.model.RestaurantLayout;
import com.quantum.model.Table;
import com.quantum.repository.RestaurantLayoutRepository;
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
    private final RestaurantLayoutRepository restaurantLayoutRepository;

    @Autowired
    public TableService(TableRepository tableRepository,
                        RestaurantLayoutRepository restaurantLayoutRepository) {
        this.tableRepository = tableRepository;
        this.restaurantLayoutRepository = restaurantLayoutRepository;
    }

    @Transactional
    public com.quantum.model.Table createTable(UUID layoutId, com.quantum.model.Table table) {
        RestaurantLayout layout = restaurantLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new EntityNotFoundException("Layout not found with ID: " + layoutId));
        table.setLayout(layout);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(table);
    }

    public List<Table> getTablesByLayout(UUID layoutId) {
        return tableRepository.findByLayoutId(layoutId);
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
        existingTable.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(existingTable);
    }

    @Transactional
    public void deleteTable(int tableId) {
        tableRepository.deleteById(tableId);
    }
}
