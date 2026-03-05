package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.dto.AuthResponse;
import com.dtsolution.godfellas.dto.LoginRequest;
import com.dtsolution.godfellas.dto.RegisterRequest;
import com.dtsolution.godfellas.entity.Artist;
import com.dtsolution.godfellas.entity.Role;
import com.dtsolution.godfellas.entity.User;
import com.dtsolution.godfellas.repository.ArtistRepository;
import com.dtsolution.godfellas.repository.UserRepository;
import com.dtsolution.godfellas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            User user = (User) authentication.getPrincipal();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            String token = jwtUtil.generateToken(user);
            
            AuthResponse response = new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getId(),
                    user.getArtist() != null ? user.getArtist().getId() : null
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        // Get current user to check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User currentUser = (User) auth.getPrincipal();
            
            // Only SUPER_ADMIN can create SUPER_ADMIN or ADMIN
            if ((request.getRole() == Role.SUPER_ADMIN || request.getRole() == Role.ADMIN) 
                    && currentUser.getRole() != Role.SUPER_ADMIN) {
                return ResponseEntity.status(403).body("Only SUPER_ADMIN can create ADMIN accounts");
            }
            
            // ADMIN can create MANAGER, ARTIST, RECEPTIONIST, VIEWER
            if (currentUser.getRole() == Role.ADMIN && 
                    (request.getRole() == Role.SUPER_ADMIN || request.getRole() == Role.ADMIN)) {
                return ResponseEntity.status(403).body("ADMIN cannot create SUPER_ADMIN or ADMIN accounts");
            }
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setActive(true);
        
        // Link to artist if provided
        if (request.getArtistId() != null) {
            Artist artist = artistRepository.findById(request.getArtistId()).orElse(null);
            user.setArtist(artist);
        }
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully");
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            AuthResponse response = new AuthResponse(
                    null,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getId(),
                    user.getArtist() != null ? user.getArtist().getId() : null
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User currentUser = (User) auth.getPrincipal();
            User userToDelete = userRepository.findById(id).orElse(null);
            
            if (userToDelete == null) {
                return ResponseEntity.notFound().build();
            }
            
            // SUPER_ADMIN can delete anyone except themselves
            if (currentUser.getRole() == Role.SUPER_ADMIN && !currentUser.getId().equals(id)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok("User deleted");
            }
            
            // ADMIN can delete MANAGER, ARTIST, RECEPTIONIST, VIEWER
            if (currentUser.getRole() == Role.ADMIN && 
                    userToDelete.getRole() != Role.SUPER_ADMIN && 
                    userToDelete.getRole() != Role.ADMIN) {
                userRepository.deleteById(id);
                return ResponseEntity.ok("User deleted");
            }
            
            return ResponseEntity.status(403).body("Insufficient permissions");
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }
}
