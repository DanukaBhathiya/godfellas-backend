package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByArtistId(Long artistId);
}
