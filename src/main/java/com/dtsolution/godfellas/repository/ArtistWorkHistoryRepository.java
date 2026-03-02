package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.ArtistWorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArtistWorkHistoryRepository extends JpaRepository<ArtistWorkHistory, Long> {
    List<ArtistWorkHistory> findByArtistIdOrderByWorkDateDesc(Long artistId);
    List<ArtistWorkHistory> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(a.artistEarning) FROM ArtistWorkHistory a WHERE a.artist.id = ?1 AND a.workDate BETWEEN ?2 AND ?3")
    BigDecimal getTotalEarningsByArtistAndDateRange(Long artistId, LocalDate startDate, LocalDate endDate);
}
