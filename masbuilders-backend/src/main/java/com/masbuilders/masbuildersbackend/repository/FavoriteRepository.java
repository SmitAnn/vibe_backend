package com.masbuilders.masbuildersbackend.repository;

import com.masbuilders.masbuildersbackend.entity.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    Optional<Favorite> findByBuyerEmailAndPropertyId(String buyerEmail, String propertyId);
    List<Favorite> findByBuyerEmail(String buyerEmail);
}
