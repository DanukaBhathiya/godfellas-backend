package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String category; // Rent, Utilities, Supplies, etc.

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    private String paymentMethod;
    private String receipt;
    private boolean isDimuExpense = false;
}