package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.repository.DailyFinancialsRepository;
import com.dtsolution.godfellas.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialCalculationService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ExpenseRepository expenseRepo;

    private static final BigDecimal STUDIO_CUT_PERCENTAGE = new BigDecimal("0.13");
    private static final BigDecimal DIMU_PERCENTAGE = new BigDecimal("0.50");
    private static final BigDecimal TATTOO_REMOVAL_30 = new BigDecimal("0.30");
    private static final BigDecimal TATTOO_REMOVAL_70 = new BigDecimal("0.70");

    public DailyFinancials calculateDailyFinancials(LocalDate date) {
        DailyFinancials financials = dailyFinancialsRepo.findByDate(date)
                .orElse(new DailyFinancials());
        
        financials.setDate(date);
        
        // Calculate total daily income: TOTAL EARNINGS + PRODUCT SALE + TATTO REMOVAL
        BigDecimal totalIncome = financials.getCustomerAdvance()
                .add(financials.getTattooing())
                .add(financials.getTattooRemoval())
                .add(financials.getPiercing())
                .add(financials.getProductSale())
                .add(financials.getEtc());
        
        BigDecimal totalEarnings = financials.getCustomerAdvance()
                .add(financials.getTattooing())
                .add(financials.getPiercing())
                .add(financials.getEtc());
        
        BigDecimal totalDailyIncome = totalEarnings.add(financials.getProductSale()).add(financials.getTattooRemoval());
        
        financials.setTotalDailyIncome(totalDailyIncome);
        financials.setTotalEarnings(totalEarnings);
        
        // Calculate 13% studio cut
        BigDecimal studioCut = totalIncome.multiply(STUDIO_CUT_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
        financials.setStudioCut(studioCut);
        
        // After studio cut
        BigDecimal afterStudioCut = totalIncome.subtract(studioCut);
        financials.setAfterStudioCut(afterStudioCut);
        
        // Artist payment (50% from after studio cut)
        BigDecimal artistPayment = afterStudioCut.multiply(DIMU_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
        financials.setArtistPayment(artistPayment);
        
        // Dimu payment (50% from after studio cut)
        BigDecimal dimuPayment = afterStudioCut.multiply(DIMU_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
        financials.setDimuPayment(dimuPayment);
        
        // Advance payment calculations
        financials.setAfterDeductionArtistPayment(
                financials.getArtistPayment().subtract(financials.getTotalAdvancePayment())
        );
        
        // Tattoo removal split
        BigDecimal tattooRemoval30 = financials.getTattooRemoval().multiply(TATTOO_REMOVAL_30)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tattooRemoval70 = financials.getTattooRemoval().multiply(TATTOO_REMOVAL_70)
                .setScale(2, RoundingMode.HALF_UP);
        
        financials.setTattooRemoval30(tattooRemoval30);
        financials.setTattooRemoval70(tattooRemoval70);
        
        // Auto-calculate expenses from Expense table (priority 1)
        // If no expenses in table, use manually entered value (priority 2)
        BigDecimal autoTotalExpenses = expenseRepo.getTotalExpensesByDate(date);
        BigDecimal autoDimuExpenses = expenseRepo.getDimuExpensesByDate(date);
        
        // Use auto-calculated if available, otherwise use manual input
        BigDecimal totalExpenses = (autoTotalExpenses != null && autoTotalExpenses.compareTo(BigDecimal.ZERO) > 0) 
            ? autoTotalExpenses 
            : (financials.getTotalExpenses() != null ? financials.getTotalExpenses() : BigDecimal.ZERO);
            
        BigDecimal dimuExpenses = (autoDimuExpenses != null && autoDimuExpenses.compareTo(BigDecimal.ZERO) > 0)
            ? autoDimuExpenses
            : (financials.getDimuExpenses() != null ? financials.getDimuExpenses() : BigDecimal.ZERO);
        
        financials.setTotalExpenses(totalExpenses);
        financials.setDimuExpenses(dimuExpenses);
        
        // Total after expenses
        BigDecimal totalAfterExpenses = totalIncome.subtract(totalExpenses);
        financials.setTotalAfterExpenses(totalAfterExpenses);
        
        // Final profit calculation: DIMU'S PAYMENT + PRODUCT SALE + TATOO REMOVAL
        BigDecimal totalProfit = dimuPayment.add(financials.getProductSale()).add(financials.getTattooRemoval());
        financials.setTotalProfit(totalProfit);
        
        return dailyFinancialsRepo.save(financials);
    }

    public DailyFinancials updateIncomeSource(LocalDate date, String source, BigDecimal amount) {
        DailyFinancials financials = dailyFinancialsRepo.findByDate(date)
                .orElse(new DailyFinancials());
        
        financials.setDate(date);
        
        switch (source.toLowerCase()) {
            case "customeradvance" -> financials.setCustomerAdvance(amount);
            case "tattooing" -> financials.setTattooing(amount);
            case "tattooremoval" -> financials.setTattooRemoval(amount);
            case "piercing" -> financials.setPiercing(amount);
            case "productsale" -> financials.setProductSale(amount);
            case "etc" -> financials.setEtc(amount);
            case "totaladvancepayment" -> financials.setTotalAdvancePayment(amount);
            case "totalexpenses" -> financials.setTotalExpenses(amount);
            case "dimuexpenses" -> financials.setDimuExpenses(amount);
            case "totalbuyingtattooproduct" -> financials.setTotalBuyingTattooProduct(amount);
            case "productpayingaftersaving" -> financials.setProductPayingAfterSaving(amount);
            default -> throw new IllegalArgumentException("Invalid income source: " + source);
        }
        
        return calculateDailyFinancials(date);
    }
}