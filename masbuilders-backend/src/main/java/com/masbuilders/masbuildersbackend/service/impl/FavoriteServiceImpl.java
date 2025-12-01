package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import com.masbuilders.masbuildersbackend.repository.FavoriteRepository;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.service.FavoriteService;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               PropertyRepository propertyRepository,
                               NotificationService notificationService) {
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Favorite addFavorite(String buyerEmail, String propertyId) {
        Favorite favorite = new Favorite();
        favorite.setBuyerEmail(buyerEmail);
        favorite.setPropertyId(propertyId);
        favorite.setCreatedAt(LocalDateTime.now());

        Favorite saved = favoriteRepository.save(favorite);

        // 🔔 Notify seller that someone favorited their property
        propertyRepository.findById(propertyId).ifPresent(property -> {
            String sellerId = property.getSellerId();
            String title = "Your property got a new favorite ❤️";
            String message = buyerEmail + " favorited your property: " + property.getTitle();
            notificationService.sendNotification(sellerId, title, message);
        });

        return saved;
    }

    @Override
    public List<Favorite> getFavorites(String buyerEmail) {
        return favoriteRepository.findByBuyerEmail(buyerEmail);
    }

    @Override
    public void removeFavorite(String buyerEmail, String propertyId) {
        favoriteRepository.deleteByBuyerEmailAndPropertyId(buyerEmail, propertyId);
    }
}
