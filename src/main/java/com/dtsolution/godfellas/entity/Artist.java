package com.dtsolution.godfellas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistCategory category; // RESIDENT / GUEST

    private String style;
    private Integer yearsOfExperience;
    private String specialization;
    private BigDecimal hourlyRate;
    private String email;
    private String contactNumber;
    private LocalDate joinDate;
    private LocalDate endDate; // For guest artists
    private boolean active = true;

    public enum ArtistCategory {
        RESIDENT, GUEST
    }
}
