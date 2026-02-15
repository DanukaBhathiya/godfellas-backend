package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDate(LocalDate date);
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Expense> findByCategory(String category);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date = ?1")
    BigDecimal getTotalExpensesByDate(LocalDate date);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date = ?1 AND e.isDimuExpense = true")
    BigDecimal getDimuExpensesByDate(LocalDate date);
}