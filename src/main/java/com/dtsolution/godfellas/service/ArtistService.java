package com.dtsolution.godfellas.service;

import com.dtsolution.godfellas.dto.ArtistDto;
import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepo;

    public Artist addArtist(ArtistDto dto) {
        Artist artist = new Artist();
        artist.setName(dto.getName());
        artist.setCategory(dto.getCategory());
        artist.setStyle(dto.getStyle());
        artist.setYearsOfExperience(dto.getYearsOfExperience());
        artist.setSpecialization(dto.getSpecialization());
        artist.setHourlyRate(dto.getHourlyRate());
        artist.setEmail(dto.getEmail());
        artist.setContactNumber(dto.getContactNumber());
        artist.setJoinDate(LocalDate.now());
        artist.setActive(dto.isActive());

        log.info("Adding artist: {} - Category: {}", dto.getName(), dto.getCategory());
        return artistRepo.save(artist);
    }

    public List<Artist> getAllArtists() {
        return artistRepo.findAll();
    }

    public List<Artist> getActiveArtists() {
        return artistRepo.findByActiveTrue();
    }

    public List<Artist> filterByCategory(Artist.ArtistCategory category) {
        return artistRepo.findByCategoryAndActiveTrue(category);
    }

    public List<Artist> getResidentArtists() {
        return artistRepo.findByCategoryAndActiveTrue(Artist.ArtistCategory.RESIDENT);
    }

    public List<Artist> getGuestArtists() {
        return artistRepo.findByCategoryAndActiveTrue(Artist.ArtistCategory.GUEST);
    }

    public Artist updateArtist(Long id, ArtistDto dto) {
        Artist artist = artistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        
        artist.setName(dto.getName());
        artist.setCategory(dto.getCategory());
        artist.setStyle(dto.getStyle());
        artist.setYearsOfExperience(dto.getYearsOfExperience());
        artist.setSpecialization(dto.getSpecialization());
        artist.setHourlyRate(dto.getHourlyRate());
        artist.setEmail(dto.getEmail());
        artist.setContactNumber(dto.getContactNumber());
        artist.setActive(dto.isActive());
        
        return artistRepo.save(artist);
    }

    public void deactivateArtist(Long id) {
        Artist artist = artistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        
        artist.setActive(false);
        if (artist.getCategory() == Artist.ArtistCategory.GUEST) {
            artist.setEndDate(LocalDate.now());
        }
        
        artistRepo.save(artist);
        log.info("Deactivated artist: {}", artist.getName());
    }
}

