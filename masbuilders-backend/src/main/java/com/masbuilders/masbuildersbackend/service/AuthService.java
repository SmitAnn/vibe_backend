package com.masbuilders.masbuildersbackend.service;


import com.masbuilders.masbuildersbackend.dto.AuthResponse;
import com.masbuilders.masbuildersbackend.dto.LoginRequest;
import com.masbuilders.masbuildersbackend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

