package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.DailyFinancials;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.DailyFinancialsRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final DailyFinancialsRepository dailyFinancialsRepo;
    private final ArtistRepository artistRepository;

    public byte[] generateDailySalesReport(LocalDate date) throws Exception {
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(date, date);
        return generateSalesReportPdf(financials, "DAILY SALES REPORT - " + date);
    }

    public byte[] generateMonthlySalesReport(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        return generateSalesReportPdf(financials, "MONTHLY SALES REPORT - " + yearMonth);
    }

    public byte[] generateYearlySalesReport(int year) throws Exception {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        return generateSalesReportPdf(financials, "YEARLY SALES REPORT - " + year);
    }

    public byte[] generateMonthlySalarySheet(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<DailyFinancials> financials = dailyFinancialsRepo.findByDateBetween(startDate, endDate);
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph("GOODFELLAS SALARY SHEET - " + yearMonth)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
        
        float[] columnWidths = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        Table table = new Table(columnWidths);
        
        String[] headers = {"ARTIST NAME", "INCOME", "AFTER 13%", "50%CUT", "ADVANCE", 
                          "ABSENT", "DEDUCTION", "TATTOO 30%", "TOTAL DED", "NET SALARY"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold()));
        }
        
        for (Artist artist : artists) {
            table.addCell(artist.getName().toUpperCase());
            table.addCell("LKR -");
            table.addCell("LKR -");
            table.addCell("LKR -");
            table.addCell("LKR -");
            table.addCell("");
            table.addCell("LKR -");
            table.addCell("LKR -");
            table.addCell("LKR -");
            table.addCell("LKR -");
        }
        
        document.add(table);
        document.close();
        
        return out.toByteArray();
    }

    private byte[] generateSalesReportPdf(List<DailyFinancials> financials, String title) throws Exception {
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph(title)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
        
        int cols = 1 + artists.size() + 19;
        Table table = new Table(cols);
        
        table.addCell(new Cell().add(new Paragraph("DATE").setBold()));
        for (Artist artist : artists) {
            table.addCell(new Cell().add(new Paragraph(artist.getName().toUpperCase()).setBold()));
        }
        
        String[] headers = {"ADVANCE", "REMOVAL", "EARNINGS", "13%CUT", "AFTER 13%", 
                          "ARTIST PAY", "DIMU PAY", "ADV PAY", "AFTER DED", "REM 30%", 
                          "REM 70%", "EXPENSES", "DIMU EXP", "AFTER EXP", "PRODUCT", 
                          "PROFIT", "DAILY INC", "BUY PROD", "PAY SAVE"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold()));
        }
        
        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        
        for (DailyFinancials f : financials) {
            table.addCell(String.valueOf(f.getDate().getDayOfMonth()));
            for (int i = 0; i < artists.size(); i++) {
                table.addCell("");
            }
            table.addCell(formatCurrency(f.getCustomerAdvance()));
            table.addCell(formatCurrency(f.getTattooRemoval()));
            table.addCell(formatCurrency(f.getTotalEarnings()));
            table.addCell(formatCurrency(f.getStudioCut()));
            table.addCell(formatCurrency(f.getAfterStudioCut()));
            table.addCell(formatCurrency(f.getArtistPayment()));
            table.addCell(formatCurrency(f.getDimuPayment()));
            table.addCell(formatCurrency(f.getTotalAdvancePayment()));
            table.addCell(formatCurrency(f.getAfterDeductionArtistPayment()));
            table.addCell(formatCurrency(f.getTattooRemoval30()));
            table.addCell(formatCurrency(f.getTattooRemoval70()));
            table.addCell(formatCurrency(f.getTotalExpenses()));
            table.addCell(formatCurrency(f.getDimuExpenses()));
            table.addCell(formatCurrency(f.getTotalAfterExpenses()));
            table.addCell(formatCurrency(f.getProductSale()));
            table.addCell(formatCurrency(f.getTotalProfit()));
            table.addCell(formatCurrency(f.getTotalDailyIncome()));
            table.addCell(formatCurrency(f.getTotalBuyingTattooProduct()));
            table.addCell(formatCurrency(f.getProductPayingAfterSaving()));
            
            totalEarnings = totalEarnings.add(f.getTotalEarnings());
            totalProfit = totalProfit.add(f.getTotalProfit());
        }
        
        document.add(table);
        document.add(new Paragraph("\nTotal Earnings: " + formatCurrency(totalEarnings)).setBold());
        document.add(new Paragraph("Total Profit: " + formatCurrency(totalProfit)).setBold());
        document.close();
        
        return out.toByteArray();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Rs0";
        }
        return String.format("Rs%,.0f", amount);
    }
}
