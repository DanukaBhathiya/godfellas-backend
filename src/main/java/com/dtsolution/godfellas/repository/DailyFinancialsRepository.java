package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.DailyFinancials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyFinancialsRepository extends JpaRepository<DailyFinancials, Long> {
    Optional<DailyFinancials> findByDate(LocalDate date);
    List<DailyFinancials> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(d.totalProfit) FROM DailyFinancials d WHERE d.date BETWEEN ?1 AND ?2")
    Double getTotalProfitBetweenDates(LocalDate startDate, LocalDate endDate);
}