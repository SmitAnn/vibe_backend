package com.masbuilders.masbuildersbackend.repository;


import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropertyRepository extends MongoRepository<Property, String> {

    List<Property> findByStatus(PropertyStatus status);

    List<Property> findBySellerId(String sellerId);
}




