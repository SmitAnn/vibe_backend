package com.masbuilders.masbuildersbackend.dto;

import com.masbuilders.masbuildersbackend.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;   // BUYER or SELLER during signup
}


