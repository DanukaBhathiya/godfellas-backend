package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.service.PdfReportService;
import com.dtsolution.godfellas.service.ProfessionalReportService;
import com.dtsolution.godfellas.service.IdenticalReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final PdfReportService pdfReportService;
    private final ProfessionalReportService professionalReportService;
    private final IdenticalReportService identicalReportService;

    @GetMapping("/sales/daily")
    public ResponseEntity<byte[]> downloadDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("Generating daily sales report for {}", date);
        
        try {
            byte[] pdfFile = identicalReportService.generateDailySalesPdf(date, date);
            String filename = String.format("daily_sales_%s.pdf", date);
            
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
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);
        } catch (Exception e) {
            log.error("Error generating monthly salary sheet: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/excel/sales/daily")
    public ResponseEntity<byte[]> downloadDailySalesExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Generating daily sales Excel report from {} to {}", startDate, endDate);
        
        try {
            byte[] excelFile = identicalReportService.generateDailySalesExcel(startDate, endDate);
            String filename = String.format("daily_sales_%s_to_%s.xlsx", startDate, endDate);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (Exception e) {
            log.error("Error generating daily sales Excel report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/excel/salary/monthly")
    public ResponseEntity<byte[]> downloadMonthlySalaryExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Generating monthly salary Excel report from {} to {}", startDate, endDate);
        
        try {
            byte[] excelFile = professionalReportService.generateMonthlySalaryReport(startDate, endDate);
            String filename = String.format("salary_report_%s_to_%s.xlsx", startDate, endDate);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (Exception e) {
            log.error("Error generating monthly salary Excel report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/excel/sales/monthly")
    public ResponseEntity<byte[]> downloadMonthlySalesExcel(
            @RequestParam int year,
            @RequestParam int month) {
        
        log.info("Generating monthly sales Excel report for {}-{}", year, month);
        
        try {
            byte[] excelFile = professionalReportService.generateMonthlySalesReport(year, month);
            String filename = String.format("monthly_sales_%d-%02d.xlsx", year, month);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (Exception e) {
            log.error("Error generating monthly sales Excel report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/excel/sales/yearly")
    public ResponseEntity<byte[]> downloadYearlySalesExcel(
            @RequestParam int year) {
        
        log.info("Generating yearly sales Excel report for {}", year);
        
        try {
            byte[] excelFile = professionalReportService.generateYearlySalesReport(year);
            String filename = String.format("yearly_sales_%d.xlsx", year);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (Exception e) {
            log.error("Error generating yearly sales Excel report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}