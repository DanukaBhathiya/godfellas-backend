package com.dtsolution.godfellas.dto;

import com.dtsolution.godfellas.entity.Artist;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtistDto {
    private String name;
    private Artist.ArtistCategory category;
    private String style;
    private Integer yearsOfExperience;
    private String specialization;
    private BigDecimal hourlyRate;
    private String email;
    private String contactNumber;
    private boolean active = true;
}
