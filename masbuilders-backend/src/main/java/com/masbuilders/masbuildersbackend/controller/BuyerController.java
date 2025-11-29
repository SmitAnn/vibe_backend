package com.masbuilders.masbuildersbackend.controller;


import com.masbuilders.masbuildersbackend.entity.Interest;
import com.masbuilders.masbuildersbackend.service.InterestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final InterestService interestService;

    public BuyerController(InterestService interestService) {
        this.interestService = interestService;
    }

    // Buyer shows interest in property
    @PostMapping("/interest")
    public Interest addInterest(@RequestBody Interest interest) {
        return interestService.addInterest(interest);
    }

    //  Buyer can see own interests
    @GetMapping("/interests/{buyerId}")
    public List<Interest> getBuyerInterests(@PathVariable String buyerId) {
        return interestService.getBuyerInterests(buyerId);
    }
}

