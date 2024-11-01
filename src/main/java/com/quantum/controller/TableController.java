package com.quantum.controller;

import com.quantum.model.Table;
import com.quantum.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping("/layouts/{layoutId}")
    public ResponseEntity<Table> createTable(@PathVariable UUID layoutId, @RequestBody Table table) {
        return ResponseEntity.ok(tableService.createTable(layoutId, table));
    }

    @GetMapping("/layouts/{layoutId}")
    public ResponseEntity<List<Table>> getTablesByLayout(@PathVariable UUID layoutId) {
        return ResponseEntity.ok(tableService.getTablesByLayout(layoutId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Table> getTableById(@PathVariable int id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Table> updateTable(@PathVariable int id, @RequestBody Table updatedTable) {
        return ResponseEntity.ok(tableService.updateTable(id, updatedTable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable int id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
