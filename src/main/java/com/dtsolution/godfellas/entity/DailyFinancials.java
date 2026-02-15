package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyFinancials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    // Income Sources
    private BigDecimal customerAdvance = BigDecimal.ZERO;
    private BigDecimal tattooing = BigDecimal.ZERO;
    private BigDecimal tattooRemoval = BigDecimal.ZERO;
    private BigDecimal piercing = BigDecimal.ZERO;
    private BigDecimal productSale = BigDecimal.ZERO;
    private BigDecimal etc = BigDecimal.ZERO;

    // Calculated fields
    private BigDecimal totalDailyIncome = BigDecimal.ZERO;
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    private BigDecimal studioCut = BigDecimal.ZERO; // 13%
    private BigDecimal afterStudioCut = BigDecimal.ZERO;
    private BigDecimal artistPayment = BigDecimal.ZERO;
    private BigDecimal dimuPayment = BigDecimal.ZERO; // 50% from net
    
    // Advance tracking
    private BigDecimal totalAdvancePayment = BigDecimal.ZERO;
    private BigDecimal afterDeductionArtistPayment = BigDecimal.ZERO;
    
    // Tattoo removal split
    private BigDecimal tattooRemoval30 = BigDecimal.ZERO;
    private BigDecimal tattooRemoval70 = BigDecimal.ZERO;
    
    // Expenses
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal dimuExpenses = BigDecimal.ZERO;
    private BigDecimal totalAfterExpenses = BigDecimal.ZERO;
    
    // Product financials
    private BigDecimal totalBuyingTattooProduct = BigDecimal.ZERO;
    private BigDecimal productPayingAfterSaving = BigDecimal.ZERO;
    
    // Final profit
    private BigDecimal totalProfit = BigDecimal.ZERO;
}