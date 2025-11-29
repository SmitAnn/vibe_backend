package com.masbuilders.masbuildersbackend.dto;



import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

