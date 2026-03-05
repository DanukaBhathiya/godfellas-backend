package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.DailyFinancialsRepository;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
public class IdenticalReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;

    // Excel Generation
    public byte[] generateDailySalesExcel(LocalDate startDate, LocalDate endDate) throws Exception {
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
            
            // Same headers for both Excel and PDF
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = getReportHeaders();
            
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Data rows
            BigDecimal[] totals = new BigDecimal[headers.length - 1];
            for (int i = 0; i < totals.length; i++) {
                totals[i] = BigDecimal.ZERO;
            }
            
            for (DailyFinancials f : financials) {
                Row row = sheet.createRow(rowNum++);
                Object[] data = getReportData(f);
                
                for (int i = 0; i < data.length; i++) {
                    if (i == 0) { // Date column
                        row.createCell(i).setCellValue(data[i].toString());
                        row.getCell(i).setCellStyle(dataStyle);
                    } else { // Currency columns
                        BigDecimal value = (BigDecimal) data[i];
                        createCurrencyCell(row, i, value, currencyStyle);
                        totals[i-1] = totals[i-1].add(value);
                    }
                }
            }
            
            // Total row
            Row totalRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalStyle);
            
            for (int i = 0; i < totals.length; i++) {
                createCurrencyCell(totalRow, i + 1, totals[i], totalStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // PDF Generation with same data
    public byte[] generateDailySalesPdf(LocalDate startDate, LocalDate endDate) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate()); // Landscape for more columns
        
        // Company Header
        document.add(new Paragraph("GOODFELLAS TATTOO STUDIO")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18));
        
        document.add(new Paragraph("123 Main Street, Colombo, Sri Lanka | Tel: +94 11 234 5678")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        
        document.add(new Paragraph("DAILY SALES REPORT")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(14));
        
        document.add(new Paragraph("Period: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                 " to " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        
        document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        
        document.add(new Paragraph(" ")); // Empty line
        
        // Same headers as Excel
        String[] headers = getReportHeaders();
        Table table = new Table(UnitValue.createPercentArray(headers.length)).useAllAvailableWidth();
        
        // Header row
        for (String header : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(header).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        // Data rows with same data as Excel
        BigDecimal[] totals = new BigDecimal[headers.length - 1];
        for (int i = 0; i < totals.length; i++) {
            totals[i] = BigDecimal.ZERO;
        }
        
        for (DailyFinancials f : financials) {
            Object[] data = getReportData(f);
            
            for (int i = 0; i < data.length; i++) {
                if (i == 0) { // Date column
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(data[i].toString()))
                            .setTextAlignment(TextAlignment.CENTER));
                } else { // Currency columns
                    BigDecimal value = (BigDecimal) data[i];
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(formatCurrency(value)))
                            .setTextAlignment(TextAlignment.RIGHT));
                    totals[i-1] = totals[i-1].add(value);
                }
            }
        }
        
        // Total row
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("TOTAL").setBold())
                .setTextAlignment(TextAlignment.CENTER));
        
        for (BigDecimal total : totals) {
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(formatCurrency(total)).setBold())
                    .setTextAlignment(TextAlignment.RIGHT));
        }
        
        document.add(table);
        document.close();
        
        return out.toByteArray();
    }

    // Common data structure for both Excel and PDF
    private String[] getReportHeaders() {
        return new String[]{
            "Date", "Total Earnings", "Studio Cut (13%)", "After Studio Cut", 
            "Artist Payment", "Customer Advance", "Tattoo Removal", "Product Sales", 
            "Total Expenses", "Net Profit"
        };
    }

    private Object[] getReportData(DailyFinancials f) {
        return new Object[]{
            f.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            f.getTotalEarnings(),
            f.getStudioCut(),
            f.getAfterStudioCut(),
            f.getArtistPayment(),
            f.getCustomerAdvance(),
            f.getTattooRemoval(),
            f.getProductSale(),
            f.getTotalExpenses(),
            f.getTotalProfit()
        };
    }

    // Monthly and Yearly methods would follow the same pattern...
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "LKR 0.00";
        }
        return String.format("LKR %,.2f", amount);
    }

    // Excel styling methods (same as ProfessionalReportService)
    private int createCompanyHeader(Sheet sheet, CellStyle titleStyle, int rowNum) {
        Row companyRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("GOODFELLAS TATTOO STUDIO");
        companyCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row addressRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell addressCell = addressRow.createCell(0);
        addressCell.setCellValue("123 Main Street, Colombo, Sri Lanka | Tel: +94 11 234 5678");
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        sheet.createRow(rowNum++);
        return rowNum;
    }

    private int createReportTitle(Sheet sheet, CellStyle titleStyle, CellStyle headerStyle, 
                                LocalDate startDate, LocalDate endDate, int rowNum, String reportTitle) {
        Row titleRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(reportTitle);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row periodRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue("Period: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                               " to " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        periodCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        Row generatedRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell generatedCell = generatedRow.createCell(0);
        generatedCell.setCellValue("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));
        
        sheet.createRow(rowNum++);
        return rowNum;
    }

    private void createCurrencyCell(Row row, int colIndex, BigDecimal value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
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