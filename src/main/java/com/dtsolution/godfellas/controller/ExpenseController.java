package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.entity.Expense;
import com.dtsolution.godfellas.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepo;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        return ResponseEntity.ok(expenseRepo.save(expense));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(expenseRepo.findByDateBetween(startDate, endDate));
        }
        return ResponseEntity.ok(expenseRepo.findAll());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Expense>> getExpensesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(expenseRepo.findByDate(date));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(expenseRepo.findByCategory(category));
    }

    @GetMapping("/today")
    public ResponseEntity<List<Expense>> getTodayExpenses() {
        return ResponseEntity.ok(expenseRepo.findByDate(LocalDate.now()));
    }
}