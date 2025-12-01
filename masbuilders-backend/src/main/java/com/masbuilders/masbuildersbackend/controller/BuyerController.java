package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Interest;
import com.masbuilders.masbuildersbackend.service.InterestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BuyerController {

    private final InterestService interestService;

    public BuyerController(InterestService interestService) {
        this.interestService = interestService;
    }

    // ✅ Buyer expresses interest in a property
    @PostMapping("/interest")
    public Interest addInterest(@RequestBody Interest interest) {
        return interestService.addInterest(interest);
    }

    // ✅ Buyer can view their interests
    @GetMapping("/interests/{buyerId}")
    public List<Interest> getBuyerInterests(@PathVariable String buyerId) {
        return interestService.getBuyerInterests(buyerId);
    }
}
