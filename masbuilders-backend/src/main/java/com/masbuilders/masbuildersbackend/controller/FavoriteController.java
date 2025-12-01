package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import com.masbuilders.masbuildersbackend.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // ❤️ Add a favorite
    @PostMapping("/add")
    public Favorite addFavorite(@RequestParam String buyerEmail, @RequestParam String propertyId) {
        return favoriteService.addFavorite(buyerEmail, propertyId);
    }

    // 💔 Remove a favorite
    @DeleteMapping("/remove")
    public void removeFavorite(@RequestParam String buyerEmail, @RequestParam String propertyId) {
        favoriteService.removeFavorite(buyerEmail, propertyId);
    }

    // 📋 Get all favorites for a buyer
    @GetMapping("/{buyerEmail}")
    public List<Favorite> getFavorites(@PathVariable String buyerEmail) {
        return favoriteService.getFavorites(buyerEmail);
    }
}
