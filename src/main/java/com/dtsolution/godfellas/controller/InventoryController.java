package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.entity.Inventory;
import com.dtsolution.godfellas.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllActiveItems());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Inventory>> getItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(inventoryService.getItemsByCategory(category));
    }
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Inventory> getItemByBarcode(@PathVariable String barcode) {
        Inventory item = inventoryService.getItemByBarcode(barcode);
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/barcode/generate")
    public ResponseEntity<String> generateBarcode(@RequestParam Long itemId) {
        String barcode = inventoryService.generateBarcode(itemId);
        return ResponseEntity.ok(barcode);
    }

    @PostMapping
    public ResponseEntity<Inventory> addItem(@RequestBody Inventory item) {
        return ResponseEntity.ok(inventoryService.addItem(item));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Inventory> updateStock(
            @PathVariable Long id, 
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(id, quantity));
    }

    @PutMapping("/{id}/restock")
    public ResponseEntity<Inventory> restockItem(
            @PathVariable Long id, 
            @RequestParam Integer additionalQuantity) {
        return ResponseEntity.ok(inventoryService.restockItem(id, additionalQuantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
        inventoryService.deactivateItem(id);
        return ResponseEntity.ok().build();
    }
}