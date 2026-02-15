package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Earnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EarningsRepository extends JpaRepository<Earnings, Long> {
    Earnings findByBillingId(Long billingId);
}
