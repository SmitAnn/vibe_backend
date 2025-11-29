package com.masbuilders.masbuildersbackend.service;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    // Buyer
    List<Property> getAllApprovedProperties();

    // Seller
    Property addProperty(Property property, String sellerId);

    List<Property> getSellerProperties(String sellerId);

    Property updateProperty(String propertyId, Property property, String sellerId);

    void deleteProperty(String propertyId, String sellerId);

    // Admin
    List<Property> getPendingProperties();

    Property approveProperty(String propertyId);

    List<Property> approveMultipleProperties(List<String> propertyIds);

    Property changeStatus(String propertyId, PropertyStatus status);

    // Search
    Page<Property> searchProperties(
            String city,
            String locality,
            String propertyType,
            Integer bhk,
            String listingType,
            Double minPrice,
            Double maxPrice,
            Double minSize,
            Double maxSize,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    // IMAGE UPLOAD
    String uploadPropertyImage(MultipartFile file);
}
