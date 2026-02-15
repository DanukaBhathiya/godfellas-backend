package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.Inventory;
import com.dtsolution.godfellas.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepo;

    public List<Inventory> getAllActiveItems() {
        return inventoryRepo.findByActiveTrue();
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepo.findLowStockItems();
    }

    public List<Inventory> getItemsByCategory(String category) {
        return inventoryRepo.findByCategory(category);
    }

    public Inventory addItem(Inventory item) {
        item.setTotalCost(item.getUnitCost().multiply(new BigDecimal(item.getQuantity())));
        item.setLastRestocked(LocalDateTime.now());
        return inventoryRepo.save(item);
    }

    public Inventory updateStock(Long itemId, Integer newQuantity) {
        Inventory item = inventoryRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        item.setQuantity(newQuantity);
        item.setTotalCost(item.getUnitCost().multiply(new BigDecimal(newQuantity)));
        item.setLastRestocked(LocalDateTime.now());
        
        return inventoryRepo.save(item);
    }

    public Inventory restockItem(Long itemId, Integer additionalQuantity) {
        Inventory item = inventoryRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        int newQuantity = item.getQuantity() + additionalQuantity;
        return updateStock(itemId, newQuantity);
    }

    public void deactivateItem(Long itemId) {
        Inventory item = inventoryRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        item.setActive(false);
        inventoryRepo.save(item);
    }
}