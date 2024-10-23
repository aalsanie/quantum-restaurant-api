package com.quantum.repository;

import com.quantum.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, UUID> {
    List<StockTransaction> findByInventoryItemId(UUID inventoryItemId);
}
