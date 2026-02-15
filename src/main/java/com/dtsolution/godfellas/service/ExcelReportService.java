package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.DailyFinancialsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;

    public byte[] generateDailySalesReport(LocalDate startDate, LocalDate endDate) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Daily Sales");
            
            // Title row
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("GOODFELLAS DAILY SALES SHEET");
            
            // Empty row
            sheet.createRow(1);
            
            // Employee code row
            Row empCodeRow = sheet.createRow(2);
            empCodeRow.createCell(0).setCellValue("EMPLOY CORD");
            for (int i = 0; i < artists.size(); i++) {
                empCodeRow.createCell(i + 1).setCellValue(String.valueOf(i + 1));
            }
            
            // Empty row
            sheet.createRow(3);
            
            // Header row
            Row headerRow = sheet.createRow(4);
            int colIndex = 0;
            headerRow.createCell(colIndex++).setCellValue("DATE");
            
            // Add artist names dynamically
            for (Artist artist : artists) {
                headerRow.createCell(colIndex++).setCellValue(artist.getName().toUpperCase());
            }
            
            // Add remaining columns
            String[] remainingHeaders = {"CUSTOMER ADVANCE", "TATTO REMOVAL", "TOTAL EARNINGS", 
                              "13%CUT", "AFTER 13%", "ARTIST PAYMENT", "DIMU'S PAYMENT", "TOTAL ADVANCE PAYMENT", 
                              "AFTER DEDUCTION ARTIST PAYMENT", "TATOO REMOVAL 30%", "TATOO RE 70%", 
                              "TOTAL EXPENSES", "DIMU EXPENSES", "TOTAL AFTER EXPENSES", "PRODUCT SALE", 
                              "TOTAL PROFIT", "TOTAL DAILY INCOM", "TOTAL BUYING TATOO PRODUCT", 
                              "PRODUCT PAYING AFTER SAVING"};
            
            for (String header : remainingHeaders) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }
            
            // Data rows
            int rowNum = 5;
            BigDecimal totalEarnings = BigDecimal.ZERO;
            BigDecimal totalStudioCut = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;
            
            for (DailyFinancials f : financials) {
                Row row = sheet.createRow(rowNum++);
                colIndex = 0;
                row.createCell(colIndex++).setCellValue(f.getDate().getDayOfMonth());
                
                // Skip artist columns (would need individual artist data)
                colIndex += artists.size();
                
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getCustomerAdvance()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTattooRemoval()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalEarnings()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getStudioCut()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getAfterStudioCut()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getArtistPayment()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getDimuPayment()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalAdvancePayment()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getAfterDeductionArtistPayment()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTattooRemoval30()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTattooRemoval70()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalExpenses()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getDimuExpenses()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalAfterExpenses()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getProductSale()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalProfit()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalDailyIncome()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getTotalBuyingTattooProduct()));
                row.createCell(colIndex++).setCellValue(formatCurrency(f.getProductPayingAfterSaving()));
                
                totalEarnings = totalEarnings.add(f.getTotalEarnings());
                totalStudioCut = totalStudioCut.add(f.getStudioCut());
                totalProfit = totalProfit.add(f.getTotalProfit());
            }
            
            // Total row
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("ALL TOTAL");
            int totalColStart = 1 + artists.size();
            totalRow.createCell(totalColStart + 2).setCellValue(formatCurrency(totalEarnings));
            totalRow.createCell(totalColStart + 3).setCellValue(formatCurrency(totalStudioCut));
            totalRow.createCell(totalColStart + 14).setCellValue(formatCurrency(totalProfit));
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateSalarySheet(LocalDate startDate, LocalDate endDate) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Salary Sheet");
            
            // Title row
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("GOODFELLAS SALARY SHEET");
            
            // Empty row
            sheet.createRow(1);
            
            // Header row
            Row headerRow = sheet.createRow(2);
            String[] headers = {"ARTIST NAME", "14 DAYS INCOME", "AFTER 13% CUT INCOME", "50%CUT", 
                              "ADVANCED TAKEN", "ABSENT DATE", "ADITIONAL OF DAY CUT AMOUNT", 
                              "TATOO REMOVEL 30%", "TOTALE DEDUCTION", "NET SALARY"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }
            
            int rowNum = 3;
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalAfter13Cut = BigDecimal.ZERO;
            BigDecimal totalNetSalary = BigDecimal.ZERO;
            
            // Add rows for each active artist
            for (Artist artist : artists) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(artist.getName().toUpperCase());
                // Would need individual artist data from database
                row.createCell(1).setCellValue("LKR -");
                row.createCell(2).setCellValue("LKR -");
                row.createCell(3).setCellValue("LKR -");
                row.createCell(4).setCellValue("LKR -");
                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue("LKR -");
                row.createCell(7).setCellValue("LKR -");
                row.createCell(8).setCellValue("LKR -");
                row.createCell(9).setCellValue("LKR -");
            }
            
            // Total row
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("TOTAL DAILY INCOM");
            
            // Add date columns dynamically based on date range
            int dateCol = 1;
            for (DailyFinancials f : financials) {
                headerRow.createCell(dateCol + headers.length - 1).setCellValue(f.getDate().toString());
                totalRow.createCell(dateCol + headers.length - 1).setCellValue(formatCurrency(f.getTotalDailyIncome()));
                dateCol++;
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Rs0";
        }
        return String.format("Rs%,.0f", amount);
    }
}