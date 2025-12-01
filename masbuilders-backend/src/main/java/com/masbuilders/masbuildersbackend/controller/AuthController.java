package com.masbuilders.masbuildersbackend.controller;


import com.masbuilders.masbuildersbackend.dto.AuthResponse;
import com.masbuilders.masbuildersbackend.dto.LoginRequest;
import com.masbuilders.masbuildersbackend.dto.RegisterRequest;
import com.masbuilders.masbuildersbackend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}


