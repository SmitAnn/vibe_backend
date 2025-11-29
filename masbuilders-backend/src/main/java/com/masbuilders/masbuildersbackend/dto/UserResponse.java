package com.masbuilders.masbuildersbackend.dto;


import com.masbuilders.masbuildersbackend.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Role role;
}

