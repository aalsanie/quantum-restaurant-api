package com.quantum.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "tables")
public class Table {

    @Id
    private int id; // Front end will set this ID and send it to the backend

    @ManyToOne
    @JoinColumn(name = "layout_id")
    private RestaurantLayout layout;

    private int tableNumber;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private Status status; // Enum: AVAILABLE, OCCUPIED

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public int getId() {
        return id;
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

    public enum Status {
        AVAILABLE, OCCUPIED
    }

    public void setId(int id) {
        this.id = id;
    }
}
