package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.dto.BillingRequest;
import com.dtsolution.godfellas.entity.Billing;
import com.dtsolution.godfellas.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<Billing> create(@RequestBody BillingRequest request) {
        return ResponseEntity.ok(billingService.createBilling(request));
    }

    @GetMapping
    public ResponseEntity<List<Billing>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(billingService.getBillingsByDateRange(startDate, endDate));
        }
        return ResponseEntity.ok(billingService.getAll());
    }
}
