package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.dto.DashboardSummary;
import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialCalculationService financialService;
    private final InventoryService inventoryService;
    private final ArtistService artistService;

    public DashboardSummary getDashboardSummary() {
        DashboardSummary summary = new DashboardSummary();
        
        // Get today's financials
        DailyFinancials todayFinancials = financialService.calculateDailyFinancials(LocalDate.now());
        summary.setTodayIncome(todayFinancials.getTotalDailyIncome());
        summary.setTodayExpenses(todayFinancials.getTotalExpenses());
        summary.setTodayProfit(todayFinancials.getTotalProfit());
        summary.setStudioCut(todayFinancials.getStudioCut());
        summary.setArtistPayments(todayFinancials.getArtistPayment());
        
        // Get inventory alerts
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        summary.setLowStockItemsCount(lowStockItems.size());
        summary.setLowStockItems(lowStockItems.stream()
                .map(Inventory::getItemName)
                .toList());
        
        // Get artist counts
        List<Artist> activeArtists = artistService.getActiveArtists();
        List<Artist> residents = artistService.getResidentArtists();
        List<Artist> guests = artistService.getGuestArtists();
        
        summary.setActiveArtistsCount(activeArtists.size());
        summary.setResidentArtistsCount(residents.size());
        summary.setGuestArtistsCount(guests.size());
        
        return summary;
    }
}