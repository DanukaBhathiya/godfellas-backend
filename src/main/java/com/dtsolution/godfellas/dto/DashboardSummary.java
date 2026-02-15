package com.dtsolution.godfellas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardSummary {
    private BigDecimal todayIncome;
    private BigDecimal todayExpenses;
    private BigDecimal todayProfit;
    private BigDecimal studioCut;
    private BigDecimal artistPayments;
    private Integer lowStockItemsCount;
    private List<String> lowStockItems;
    private Integer activeArtistsCount;
    private Integer residentArtistsCount;
    private Integer guestArtistsCount;
}