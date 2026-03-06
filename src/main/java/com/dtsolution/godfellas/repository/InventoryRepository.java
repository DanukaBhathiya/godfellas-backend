package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByActiveTrue();
    List<Inventory> findByCategory(String category);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minStockLevel AND i.active = true")
    List<Inventory> findLowStockItems();
    
    Inventory findByBarcode(String barcode);
}