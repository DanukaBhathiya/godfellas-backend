package com.dtsolution.godfellas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeUpdateRequest {
    private LocalDate date;
    private BigDecimal customerAdvance;
    private BigDecimal tattooing;
    private BigDecimal tattooRemoval;
    private BigDecimal piercing;
    private BigDecimal productSale;
    private BigDecimal etc;
}