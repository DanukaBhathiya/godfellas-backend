package com.dtsolution.godfellas.dto;

import com.dtsolution.godfellas.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
    private Long artistId; // Optional: link to artist if role is ARTIST
}
