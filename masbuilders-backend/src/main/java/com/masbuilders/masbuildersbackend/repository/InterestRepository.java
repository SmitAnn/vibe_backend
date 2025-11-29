package com.masbuilders.masbuildersbackend.repository;



import com.masbuilders.masbuildersbackend.entity.Interest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InterestRepository extends MongoRepository<Interest, String> {

    List<Interest> findBySellerId(String sellerId);

    List<Interest> findByBuyerId(String buyerId);
}

