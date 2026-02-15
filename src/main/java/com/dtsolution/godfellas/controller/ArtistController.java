package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.dto.ArtistDto;
import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    public ResponseEntity<Artist> add(@RequestBody ArtistDto dto) {
        return ResponseEntity.ok(artistService.addArtist(dto));
    }

    @GetMapping
    public ResponseEntity<List<Artist>> all() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Artist>> getActiveArtists() {
        return ResponseEntity.ok(artistService.getActiveArtists());
    }

    @GetMapping("/residents")
    public ResponseEntity<List<Artist>> getResidentArtists() {
        return ResponseEntity.ok(artistService.getResidentArtists());
    }

    @GetMapping("/guests")
    public ResponseEntity<List<Artist>> getGuestArtists() {
        return ResponseEntity.ok(artistService.getGuestArtists());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Artist>> filter(@RequestParam Artist.ArtistCategory category) {
        return ResponseEntity.ok(artistService.filterByCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> update(@PathVariable Long id, @RequestBody ArtistDto dto) {
        return ResponseEntity.ok(artistService.updateArtist(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        artistService.deactivateArtist(id);
        return ResponseEntity.ok().build();
    }
}
