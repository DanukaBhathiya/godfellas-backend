package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.entity.ArtistWorkHistory;
import com.dtsolution.godfellas.repository.ArtistWorkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/artist-work-history")
@RequiredArgsConstructor
public class ArtistWorkHistoryController {

    private final ArtistWorkHistoryRepository workHistoryRepo;

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<ArtistWorkHistory>> getArtistWorkHistory(@PathVariable Long artistId) {
        return ResponseEntity.ok(workHistoryRepo.findByArtistIdOrderByWorkDateDesc(artistId));
    }

    @GetMapping("/artist/{artistId}/earnings")
    public ResponseEntity<BigDecimal> getArtistEarnings(
            @PathVariable Long artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal earnings = workHistoryRepo.getTotalEarningsByArtistAndDateRange(artistId, startDate, endDate);
        return ResponseEntity.ok(earnings != null ? earnings : BigDecimal.ZERO);
    }
}
