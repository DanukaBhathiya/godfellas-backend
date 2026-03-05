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
public class ExcelToPdfReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;

    public byte[] generateDailySalesReportPdf(LocalDate date) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(date, date);
        return generateSalesReportPdf(financials, "DAILY SALES REPORT - " + date);
    }

    public byte[] generateMonthlySalesReportPdf(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        return generateSalesReportPdf(financials, "MONTHLY SALES REPORT - " + yearMonth);
    }

    public byte[] generateYearlySalesReportPdf(int year) throws Exception {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        return generateSalesReportPdf(financials, "YEARLY SALES REPORT - " + year);
    }

    public byte[] generateMonthlySalarySheetPdf(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Monthly Salary Report");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            int rowNum = 0;
            rowNum = createCompanyHeader(sheet, titleStyle, rowNum);
            rowNum = createReportTitle(sheet, titleStyle, headerStyle, startDate, endDate, rowNum, "MONTHLY SALARY REPORT");
            
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Artist Name", "Employee ID", "Total Income", "Studio Cut (13%)", 
                "Net Income", "Advances Taken", "Deductions", "Final Salary"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            BigDecimal totalSalaries = BigDecimal.ZERO;
            int empId = 1;
            
            for (Artist artist : artists) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(artist.getName());
                row.getCell(0).setCellStyle(dataStyle);
                
                row.createCell(1).setCellValue("EMP" + String.format("%03d", empId++));
                row.getCell(1).setCellStyle(dataStyle);
                
                BigDecimal salary = new BigDecimal("50000");
                createCurrencyCell(row, 2, salary, currencyStyle);
                createCurrencyCell(row, 3, salary.multiply(new BigDecimal("0.13")), currencyStyle);
                createCurrencyCell(row, 4, salary.multiply(new BigDecimal("0.87")), currencyStyle);
                createCurrencyCell(row, 5, BigDecimal.ZERO, currencyStyle);
                createCurrencyCell(row, 6, BigDecimal.ZERO, currencyStyle);
                createCurrencyCell(row, 7, salary.multiply(new BigDecimal("0.87")), currencyStyle);
                
                totalSalaries = totalSalaries.add(salary.multiply(new BigDecimal("0.87")));
            }
            
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL SALARIES");
            totalLabelCell.setCellStyle(totalStyle);
            
            createCurrencyCell(totalRow, 7, totalSalaries, totalStyle);
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to PDF-like format by setting print area and page setup
            sheet.getPrintSetup().setLandscape(true);
            sheet.setFitToPage(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private byte[] generateSalesReportPdf(List<DailyFinancials> financials, String title) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales Report");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            int rowNum = 0;
            
            // Company Header
            Row companyRow = sheet.createRow(rowNum++);
            Cell companyCell = companyRow.createCell(0);
            companyCell.setCellValue("GOODFELLAS TATTOO STUDIO");
            companyCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
            
            Row addressRow = sheet.createRow(rowNum++);
            Cell addressCell = addressRow.createCell(0);
            addressCell.setCellValue("123 Main Street, Colombo, Sri Lanka | Tel: +94 11 234 5678");
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
            
            sheet.createRow(rowNum++); // Empty row
            
            // Report Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
            
            Row generatedRow = sheet.createRow(rowNum++);
            Cell generatedCell = generatedRow.createCell(0);
            generatedCell.setCellValue("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
            
            sheet.createRow(rowNum++); // Empty row
            
            // Headers
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Date", "Total Earnings", "Studio Cut (13%)", "After Studio Cut", 
                "Artist Payment", "Customer Advance", "Tattoo Removal", "Product Sales", 
                "Total Expenses", "Net Profit"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            BigDecimal totalEarnings = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;
            BigDecimal totalExpenses = BigDecimal.ZERO;
            
            for (DailyFinancials f : financials) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(f.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.getCell(0).setCellStyle(dataStyle);
                
                createCurrencyCell(row, 1, f.getTotalEarnings(), currencyStyle);
                createCurrencyCell(row, 2, f.getStudioCut(), currencyStyle);
                createCurrencyCell(row, 3, f.getAfterStudioCut(), currencyStyle);
                createCurrencyCell(row, 4, f.getArtistPayment(), currencyStyle);
                createCurrencyCell(row, 5, f.getCustomerAdvance(), currencyStyle);
                createCurrencyCell(row, 6, f.getTattooRemoval(), currencyStyle);
                createCurrencyCell(row, 7, f.getProductSale(), currencyStyle);
                createCurrencyCell(row, 8, f.getTotalExpenses(), currencyStyle);
                createCurrencyCell(row, 9, f.getTotalProfit(), currencyStyle);
                
                totalEarnings = totalEarnings.add(f.getTotalEarnings());
                totalProfit = totalProfit.add(f.getTotalProfit());
                totalExpenses = totalExpenses.add(f.getTotalExpenses());
            }
            
            // Total row
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalStyle);
            
            createCurrencyCell(totalRow, 1, totalEarnings, totalStyle);
            createCurrencyCell(totalRow, 8, totalExpenses, totalStyle);
            createCurrencyCell(totalRow, 9, totalProfit, totalStyle);
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Set up for PDF-like printing
            sheet.getPrintSetup().setLandscape(true);
            sheet.setFitToPage(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private int createCompanyHeader(Sheet sheet, CellStyle titleStyle, int rowNum) {
        Row companyRow = sheet.createRow(rowNum++);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("GOODFELLAS TATTOO STUDIO");
        companyCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row addressRow = sheet.createRow(rowNum++);
        Cell addressCell = addressRow.createCell(0);
        addressCell.setCellValue("123 Main Street, Colombo, Sri Lanka | Tel: +94 11 234 5678");
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        sheet.createRow(rowNum++);
        return rowNum;
    }

    private int createReportTitle(Sheet sheet, CellStyle titleStyle, CellStyle headerStyle, 
                                LocalDate startDate, LocalDate endDate, int rowNum, String reportTitle) {
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(reportTitle);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row periodRow = sheet.createRow(rowNum++);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue("Period: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                               " to " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        periodCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row generatedRow = sheet.createRow(rowNum++);
        Cell generatedCell = generatedRow.createCell(0);
        generatedCell.setCellValue("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        sheet.createRow(rowNum++);
        return rowNum;
    }

    private void createCurrencyCell(Row row, int colIndex, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("\"LKR \"#,##0.00"));
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("\"LKR \"#,##0.00"));
        return style;
    }
}