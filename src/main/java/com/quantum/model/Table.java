package com.quantum.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@jakarta.persistence.Table(name = "tables")
public class Table {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "layout_id")
    private RestaurantLayout layout;

    private int tableNumber;
    private int capacity;

    @Enumerated(EnumType.STRING)
    private Status status; // Enum: AVAILABLE, OCCUPIED

    @Enumerated(EnumType.STRING)
    private TableType tableType; // Enum: ROUND_BAR, REGULAR

    private int floorNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        AVAILABLE, OCCUPIED
    }

    public enum TableType {
        ROUND_BAR, REGULAR
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RestaurantLayout getLayout() {
        return layout;
    }

    public void setLayout(RestaurantLayout layout) {
        this.layout = layout;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
