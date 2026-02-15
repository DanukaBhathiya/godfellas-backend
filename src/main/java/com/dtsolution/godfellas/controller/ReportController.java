package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.service.PdfReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller for generating and downloading PDF reports.
 * Handles daily, monthly, yearly sales reports and monthly salary sheets.
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final PdfReportService pdfReportService;

    @GetMapping("/sales/daily")
    public ResponseEntity<byte[]> downloadDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("Generating daily sales report for {}", date);
        
        try {
            byte[] pdfFile = pdfReportService.generateDailySalesReport(date);
            String filename = String.format("daily_sales_%s.pdf", date);
            
            log.info("Daily sales report generated: {}", filename);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);
        } catch (Exception e) {
            log.error("Error generating daily sales report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<byte[]> downloadMonthlySalesReport(
            @RequestParam int year,
            @RequestParam int month) {
        
        log.info("Generating monthly sales report for {}-{}", year, month);
        
        try {
            byte[] pdfFile = pdfReportService.generateMonthlySalesReport(year, month);
            String filename = String.format("monthly_sales_%d-%02d.pdf", year, month);
            
            log.info("Monthly sales report generated: {}", filename);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);
        } catch (Exception e) {
            log.error("Error generating monthly sales report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/yearly")
    public ResponseEntity<byte[]> downloadYearlySalesReport(
            @RequestParam int year) {
        
        log.info("Generating yearly sales report for {}", year);
        
        try {
            byte[] pdfFile = pdfReportService.generateYearlySalesReport(year);
            String filename = String.format("yearly_sales_%d.pdf", year);
            
            log.info("Yearly sales report generated: {}", filename);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);
        } catch (Exception e) {
            log.error("Error generating yearly sales report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/salary/monthly")
    public ResponseEntity<byte[]> downloadMonthlySalarySheet(
            @RequestParam int year,
            @RequestParam int month) {
        
        log.info("Generating monthly salary sheet for {}-{}", year, month);
        
        try {
            byte[] pdfFile = pdfReportService.generateMonthlySalarySheet(year, month);
            String filename = String.format("salary_sheet_%d-%02d.pdf", year, month);
            
            log.info("Monthly salary sheet generated: {}", filename);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);
        } catch (Exception e) {
            log.error("Error generating monthly salary sheet: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}