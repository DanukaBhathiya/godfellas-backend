package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.service.FinancialCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/financials")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialCalculationService financialService;

    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyFinancials> getDailyFinancials(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(financialService.calculateDailyFinancials(date));
    }

    @PostMapping("/income/{date}")
    public ResponseEntity<DailyFinancials> updateIncomeSource(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String source,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(financialService.updateIncomeSource(date, source, amount));
    }

    @GetMapping("/today")
    public ResponseEntity<DailyFinancials> getTodayFinancials() {
        return ResponseEntity.ok(financialService.calculateDailyFinancials(LocalDate.now()));
    }
}