package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Earnings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Billing billing;

    private BigDecimal totalEarnings;
    private BigDecimal studioCut;
    private BigDecimal artistPayment;
    private BigDecimal advancePayment;
}
