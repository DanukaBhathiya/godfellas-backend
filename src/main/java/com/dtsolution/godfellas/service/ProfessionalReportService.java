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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;

    public byte[] generateDailySalesReport(LocalDate startDate, LocalDate endDate) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Daily Sales Report");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            int rowNum = 0;
            rowNum = createCompanyHeader(sheet, titleStyle, rowNum);
            rowNum = createReportTitle(sheet, titleStyle, headerStyle, startDate, endDate, rowNum, "DAILY SALES REPORT");
            
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
            
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalStyle);
            
            createCurrencyCell(totalRow, 1, totalEarnings, totalStyle);
            createCurrencyCell(totalRow, 8, totalExpenses, totalStyle);
            createCurrencyCell(totalRow, 9, totalProfit, totalStyle);
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateMonthlySalaryReport(LocalDate startDate, LocalDate endDate) throws Exception {
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
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateMonthlySalesReport(int year, int month) throws Exception {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Monthly Sales Report");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            int rowNum = 0;
            rowNum = createCompanyHeader(sheet, titleStyle, rowNum);
            rowNum = createReportTitle(sheet, titleStyle, headerStyle, startDate, endDate, rowNum, "MONTHLY SALES REPORT");
            
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Date", "Earnings", "Expenses", "Profit"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            for (DailyFinancials f : financials) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(f.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.getCell(0).setCellStyle(dataStyle);
                createCurrencyCell(row, 1, f.getTotalEarnings(), currencyStyle);
                createCurrencyCell(row, 2, f.getTotalExpenses(), currencyStyle);
                createCurrencyCell(row, 3, f.getTotalProfit(), currencyStyle);
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateYearlySalesReport(int year) throws Exception {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Yearly Sales Report");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            int rowNum = 0;
            rowNum = createCompanyHeader(sheet, titleStyle, rowNum);
            rowNum = createReportTitle(sheet, titleStyle, headerStyle, startDate, endDate, rowNum, "YEARLY SALES REPORT - " + year);
            
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Month", "Days", "Earnings", "Profit"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            for (int month = 1; month <= 12; month++) {
                final int currentMonth = month;
                List<DailyFinancials> monthlyData = financials.stream()
                    .filter(f -> f.getDate().getMonthValue() == currentMonth)
                    .collect(java.util.stream.Collectors.toList());
                
                if (!monthlyData.isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(java.time.Month.of(month).name());
                    row.getCell(0).setCellStyle(dataStyle);
                    row.createCell(1).setCellValue(monthlyData.size());
                    row.getCell(1).setCellStyle(dataStyle);
                    
                    BigDecimal monthEarnings = monthlyData.stream().map(DailyFinancials::getTotalEarnings).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal monthProfit = monthlyData.stream().map(DailyFinancials::getTotalProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    createCurrencyCell(row, 2, monthEarnings, currencyStyle);
                    createCurrencyCell(row, 3, monthProfit, currencyStyle);
                }
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
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