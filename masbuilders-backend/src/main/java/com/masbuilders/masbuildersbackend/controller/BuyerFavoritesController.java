package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.service.FavoriteService;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/buyer/favorites")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BuyerFavoritesController {

    private final FavoriteService favoriteService;
    private final PropertyService propertyService;
    private final NotificationService notificationService;

    public BuyerFavoritesController(
            FavoriteService favoriteService,
            PropertyService propertyService,
            NotificationService notificationService
    ) {
        this.favoriteService = favoriteService;
        this.propertyService = propertyService;
        this.notificationService = notificationService;
    }

    // ✅ Add to favorites + send notification to seller
    @PostMapping("/add")
    public Favorite addFavorite(
            @RequestParam String buyerEmail,
            @RequestParam String propertyId
    ) {
        Favorite saved = favoriteService.addFavorite(buyerEmail, propertyId);

        // ✅ Send notification to seller
        try {
            Property property = propertyService.getPropertyById(propertyId);
            String sellerId = property.getSellerId();

            notificationService.sendNotification(
                    sellerId,
                    "💖 New Favorite Added!",
                    "Your property '" + property.getTitle() + "' was liked by " + buyerEmail + "."
            );

            System.out.println("💌 Notification sent to seller: " + sellerId);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send favorite notification: " + e.getMessage());
        }

        return saved;
    }

    // ✅ Remove from favorites
    @DeleteMapping("/remove")
    public void removeFavorite(
            @RequestParam String buyerEmail,
            @RequestParam String propertyId
    ) {
        favoriteService.removeFavorite(buyerEmail, propertyId);
    }

    // ✅ Get all favorites for a buyer
    @GetMapping("/{buyerEmail}")
    public List<Favorite> getFavorites(@PathVariable String buyerEmail) {
        return favoriteService.getFavorites(buyerEmail);
    }
}
