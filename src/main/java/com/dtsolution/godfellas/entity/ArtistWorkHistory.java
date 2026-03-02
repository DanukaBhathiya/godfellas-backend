package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistWorkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Artist artist;

    @ManyToOne
    private Billing billing;

    @Column(nullable = false)
    private LocalDate workDate;

    private String serviceType;
    private BigDecimal amount;
    private BigDecimal artistEarning;
    private String notes;
}
