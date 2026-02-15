package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
