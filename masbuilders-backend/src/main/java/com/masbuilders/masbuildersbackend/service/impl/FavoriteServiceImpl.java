package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.repository.FavoriteRepository;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.service.FavoriteService;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;

    public FavoriteServiceImpl(
            FavoriteRepository favoriteRepository,
            PropertyRepository propertyRepository,
            NotificationService notificationService
    ) {
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Favorite addFavorite(String buyerEmail, String propertyId) {
        // ✅ Prevent duplicate favorites
        Optional<Favorite> existing = favoriteRepository.findByBuyerEmailAndPropertyId(buyerEmail, propertyId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Favorite favorite = new Favorite();
        favorite.setBuyerEmail(buyerEmail);
        favorite.setPropertyId(propertyId);
        favorite.setCreatedAt(Instant.now()); // ✅ Use Instant, not LocalDateTime

        Favorite savedFavorite = favoriteRepository.save(favorite);

        // ✅ Send notification to the seller
        try {
            notificationService.notifySellerOnFavorite(propertyId, buyerEmail);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send favorite notification: " + e.getMessage());
        }

        return savedFavorite;
    }

    @Override
    public List<Favorite> getFavorites(String buyerEmail) {
        return favoriteRepository.findByBuyerEmail(buyerEmail);
    }

    @Override
    public void removeFavorite(String buyerEmail, String propertyId) {
        Optional<Favorite> favorite = favoriteRepository.findByBuyerEmailAndPropertyId(buyerEmail, propertyId);
        favorite.ifPresent(favoriteRepository::delete);
    }
}
