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
        List<Artist> artists = artistRepository.findByActiveTrue();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Title
        document.add(new Paragraph("GOODFELLAS TATTOO STUDIO")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18));
        
        document.add(new Paragraph("MONTHLY SALARY SHEET - " + yearMonth)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(14));
        
        document.add(new Paragraph("Generated on: " + LocalDate.now())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        
        document.add(new Paragraph(" "));
        
        // Create compact salary table
        float[] columnWidths = {3, 2, 3, 3, 2, 3};
        Table table = new Table(columnWidths);
        
        String[] headers = {"Artist Name", "Emp ID", "Total Income", "Studio Cut (13%)", "Advances", "Net Salary"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        BigDecimal totalSalaries = BigDecimal.ZERO;
        int empId = 1;
        
        for (Artist artist : artists) {
            BigDecimal income = new BigDecimal("50000"); // Placeholder
            BigDecimal studioCut = income.multiply(new BigDecimal("0.13"));
            BigDecimal netSalary = income.subtract(studioCut);
            
            table.addCell(new Cell().add(new Paragraph(artist.getName()))
                    .setTextAlignment(TextAlignment.LEFT));
            table.addCell(new Cell().add(new Paragraph("EMP" + String.format("%03d", empId++)))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(income)))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(studioCut)))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph("Rs0"))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(netSalary)))
                    .setTextAlignment(TextAlignment.RIGHT));
            
            totalSalaries = totalSalaries.add(netSalary);
        }
        
        // Total row
        table.addCell(new Cell().add(new Paragraph("TOTAL").setBold())
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell().add(new Paragraph(""))); // Empty cell
        table.addCell(new Cell().add(new Paragraph(""))); // Empty cell
        table.addCell(new Cell().add(new Paragraph(""))); // Empty cell
        table.addCell(new Cell().add(new Paragraph(""))); // Empty cell
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalSalaries)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        
        document.add(table);
        
        // Summary
        document.add(new Paragraph(" "));
        document.add(new Paragraph("SUMMARY")
                .setBold()
                .setFontSize(12));
        document.add(new Paragraph("Total Artists: " + artists.size()));
        document.add(new Paragraph("Total Salary Payout: " + formatCurrency(totalSalaries)));
        
        document.close();
        return out.toByteArray();
    }

    private byte[] generateSalesReportPdf(List<DailyFinancials> financials, String title) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Title
        document.add(new Paragraph("GOODFELLAS TATTOO STUDIO")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18));
        
        document.add(new Paragraph(title)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(14));
        
        document.add(new Paragraph("Generated on: " + LocalDate.now())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        
        document.add(new Paragraph(" "));
        
        // Create a compact table with essential columns only
        float[] columnWidths = {2, 3, 3, 3, 3, 3};
        Table table = new Table(columnWidths);
        
        // Headers
        String[] headers = {"Date", "Total Earnings", "Studio Cut", "Artist Payment", "Expenses", "Net Profit"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal totalStudioCut = BigDecimal.ZERO;
        BigDecimal totalArtistPayment = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        
        // Data rows
        for (DailyFinancials f : financials) {
            table.addCell(new Cell().add(new Paragraph(f.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"))))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(f.getTotalEarnings())))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(f.getStudioCut())))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(f.getArtistPayment())))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(f.getTotalExpenses())))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(f.getTotalProfit())))
                    .setTextAlignment(TextAlignment.RIGHT));
            
            totalEarnings = totalEarnings.add(f.getTotalEarnings());
            totalStudioCut = totalStudioCut.add(f.getStudioCut());
            totalArtistPayment = totalArtistPayment.add(f.getArtistPayment());
            totalExpenses = totalExpenses.add(f.getTotalExpenses());
            totalProfit = totalProfit.add(f.getTotalProfit());
        }
        
        // Total row
        table.addCell(new Cell().add(new Paragraph("TOTAL").setBold())
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalEarnings)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalStudioCut)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalArtistPayment)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalExpenses)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().add(new Paragraph(formatCurrency(totalProfit)).setBold())
                .setTextAlignment(TextAlignment.RIGHT));
        
        document.add(table);
        
        // Summary section
        document.add(new Paragraph(" "));
        document.add(new Paragraph("SUMMARY")
                .setBold()
                .setFontSize(12));
        document.add(new Paragraph("Total Days: " + financials.size()));
        document.add(new Paragraph("Average Daily Earnings: " + 
                formatCurrency(financials.isEmpty() ? BigDecimal.ZERO : 
                totalEarnings.divide(new BigDecimal(financials.size()), 2, java.math.RoundingMode.HALF_UP))));
        
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
