package com.quantum.repository;

import com.quantum.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
    List<Table> findByLayoutId(UUID layoutId);
}
