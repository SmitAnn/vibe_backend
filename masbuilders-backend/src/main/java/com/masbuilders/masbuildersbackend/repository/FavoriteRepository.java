package com.masbuilders.masbuildersbackend.repository;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByBuyerEmail(String buyerEmail);
    void deleteByBuyerEmailAndPropertyId(String buyerEmail, String propertyId);
}
