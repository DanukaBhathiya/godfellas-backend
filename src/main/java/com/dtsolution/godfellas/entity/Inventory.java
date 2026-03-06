package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    private String category; // Needles, Ink, Aftercare, etc.
    private String supplier;
    
    @Column(unique = true)
    private String barcode; // Optional barcode for scanning
    
    private Integer quantity;
    private Integer minStockLevel = 10;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private LocalDateTime lastRestocked;
    private String notes;
    private boolean active = true;
}