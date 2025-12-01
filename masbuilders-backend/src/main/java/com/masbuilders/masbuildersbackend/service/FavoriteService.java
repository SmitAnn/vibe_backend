package com.masbuilders.masbuildersbackend.service;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(String buyerEmail, String propertyId);
    List<Favorite> getFavorites(String buyerEmail);
    void removeFavorite(String buyerEmail, String propertyId);
}
