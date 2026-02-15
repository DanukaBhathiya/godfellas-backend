package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.AdvancePayment;
import com.dtsolution.godfellas.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, Long> {
    List<AdvancePayment> findByArtist(Artist artist);
    List<AdvancePayment> findBySettledFalse();
    
    @Query("SELECT SUM(a.amount) FROM AdvancePayment a WHERE a.artist = ?1 AND a.settled = false")
    BigDecimal getTotalUnsettledAdvancesByArtist(Artist artist);
}