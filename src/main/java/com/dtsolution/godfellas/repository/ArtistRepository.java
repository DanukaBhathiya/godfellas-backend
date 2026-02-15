package com.dtsolution.godfellas.repository;

import com.dtsolution.godfellas.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByCategory(Artist.ArtistCategory category);
    List<Artist> findByActiveTrue();
    List<Artist> findByCategoryAndActiveTrue(Artist.ArtistCategory category);
}
