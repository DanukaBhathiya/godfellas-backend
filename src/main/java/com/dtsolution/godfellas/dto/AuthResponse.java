package com.dtsolution.godfellas.dto;

import com.dtsolution.godfellas.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private Role role;
    private Long userId;
    private Long artistId; // Only for ARTIST role
}
