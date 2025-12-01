package com.masbuilders.masbuildersbackend.controller;



import com.masbuilders.masbuildersbackend.entity.Interest;
import com.masbuilders.masbuildersbackend.service.InterestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SellerController {

    private final InterestService interestService;

    public SellerController(InterestService interestService) {
        this.interestService = interestService;
    }

    // Seller sees who is interested in their properties
    @GetMapping("/interests/{sellerId}")
    public List<Interest> getSellerInterests(@PathVariable String sellerId) {
        return interestService.getSellerInterests(sellerId);
    }
}


