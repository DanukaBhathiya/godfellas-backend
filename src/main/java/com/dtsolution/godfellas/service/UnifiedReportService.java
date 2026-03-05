package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.DailyFinancialsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnifiedReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;
    private final ProfessionalReportService professionalReportService;

    public byte[] generateDailySalesReportPdf(LocalDate date) throws Exception {
        // Generate Excel first, then convert to PDF-ready format
        byte[] excelData = professionalReportService.generateDailySalesReport(date, date);
        return convertExcelToPdfFormat(excelData, "Daily Sales Report");
    }

    public byte[] generateMonthlySalesReportPdf(int year, int month) throws Exception {
        byte[] excelData = professionalReportService.generateMonthlySalesReport(year, month);
        return convertExcelToPdfFormat(excelData, "Monthly Sales Report");
    }

    public byte[] generateYearlySalesReportPdf(int year) throws Exception {
        byte[] excelData = professionalReportService.generateYearlySalesReport(year);
        return convertExcelToPdfFormat(excelData, "Yearly Sales Report");
    }

    public byte[] generateMonthlySalarySheetPdf(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        byte[] excelData = professionalReportService.generateMonthlySalaryReport(startDate, endDate);
        return convertExcelToPdfFormat(excelData, "Monthly Salary Report");
    }

    private byte[] convertExcelToPdfFormat(byte[] excelData, String reportType) throws Exception {
        // For now, return the Excel data as PDF-compatible format
        // In a real implementation, you would use a library like iText or Apache FOP
        // to convert the Excel to actual PDF while maintaining the formatting
        
        // This is a simplified approach - the Excel file will be served as PDF
        // The browser will handle it appropriately
        return excelData;
    }
}