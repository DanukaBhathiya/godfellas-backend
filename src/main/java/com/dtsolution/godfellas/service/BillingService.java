package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.dto.BillingRequest;
import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.Billing;
import com.dtsolution.godfellas.entity.Client;
import com.dtsolution.godfellas.entity.Earnings;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.BillingRepository;
import com.dtsolution.godfellas.repository.ClientRepository;
import com.dtsolution.godfellas.repository.EarningsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final BillingRepository billingRepo;
    private final ClientRepository clientRepo;
    private final ArtistRepository artistRepo;
    private final EarningsRepository earningsRepo;
    private final FinancialCalculationService financialService;

    public Billing createBilling(BillingRequest request) {
        log.info("Creating billing for client {} and artist {}", request.getClientId(), request.getArtistId());

        Client client = clientRepo.findById(request.getClientId()).orElseThrow();
        Artist artist = artistRepo.findById(request.getArtistId()).orElseThrow();

        Billing billing = new Billing();
        billing.setClient(client);
        billing.setArtist(artist);
        billing.setBillingDate(LocalDate.now());
        billing.setAmount(request.getAmount());
        billing.setPaymentMethod(request.getPaymentMethod());
        billing.setServiceType(request.getServiceType());

        Billing savedBilling = billingRepo.save(billing);

        // Calculate earnings
        BigDecimal studioCut = request.getAmount().multiply(new BigDecimal("0.13"));
        BigDecimal artistPay = request.getAmount().subtract(studioCut);

        Earnings earnings = new Earnings();
        earnings.setBilling(savedBilling);
        earnings.setTotalEarnings(request.getAmount());
        earnings.setStudioCut(studioCut);
        earnings.setArtistPayment(artistPay);
        earnings.setAdvancePayment(BigDecimal.ZERO);

        earningsRepo.save(earnings);

        // Update daily financials based on service type
        updateDailyFinancials(request.getServiceType(), request.getAmount());

        log.info("Billing and earnings saved successfully");
        return savedBilling;
    }

    private void updateDailyFinancials(String serviceType, BigDecimal amount) {
        LocalDate today = LocalDate.now();
        String incomeSource = switch (serviceType.toLowerCase()) {
            case "tattoo" -> "tattooing";
            case "removal" -> "tattooremoval";
            case "piercing" -> "piercing";
            case "product" -> "productsale";
            default -> "etc";
        };
        
        financialService.updateIncomeSource(today, incomeSource, amount);
    }

    public List<Billing> getAll() {
        return billingRepo.findAll();
    }

    public List<Billing> getBillingsByDate(LocalDate date) {
        return billingRepo.findAll().stream()
                .filter(billing -> billing.getBillingDate().equals(date))
                .toList();
    }

    public List<Billing> getBillingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return billingRepo.findAll().stream()
                .filter(billing -> !billing.getBillingDate().isBefore(startDate) && 
                                   !billing.getBillingDate().isAfter(endDate))
                .toList();
    }
}
