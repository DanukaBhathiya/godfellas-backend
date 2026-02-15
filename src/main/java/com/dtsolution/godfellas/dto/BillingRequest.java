package com.dtsolution.godfellas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillingRequest {
    private Long clientId;
    private Long artistId;
    private BigDecimal amount;
    private String paymentMethod;
    private String serviceType;
}
