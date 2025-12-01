package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import com.masbuilders.masbuildersbackend.exception.ResourceNotFoundException;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final MongoTemplate mongoTemplate;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               MongoTemplate mongoTemplate) {
        this.propertyRepository = propertyRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // ----------------- BUYER -----------------

    @Override
    public List<Property> getAllApprovedProperties() {
        return propertyRepository.findByStatus(PropertyStatus.APPROVED);
    }

    // ----------------- SELLER -----------------

    @Override
    public Property addProperty(Property property, String sellerId) {
        property.setId(null);
        property.setSellerId(sellerId);
        property.setStatus(PropertyStatus.PENDING);

        if (property.getCreatedAt() == null) {
            property.setCreatedAt(Instant.now());
        }

        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getSellerProperties(String sellerId) {
        return propertyRepository.findBySellerId(sellerId);
    }

    @Override
    public Property updateProperty(String propertyId, Property updatedProperty, String sellerId) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("You can update only your own properties");
        }

        existing.setTitle(updatedProperty.getTitle());
        existing.setDescription(updatedProperty.getDescription());
        existing.setCity(updatedProperty.getCity());
        existing.setLocality(updatedProperty.getLocality());
        existing.setAddress(updatedProperty.getAddress());
        existing.setPrice(updatedProperty.getPrice());
        existing.setSizeSqFt(updatedProperty.getSizeSqFt());
        existing.setPropertyType(updatedProperty.getPropertyType());
        existing.setListingType(updatedProperty.getListingType());
        existing.setBhk(updatedProperty.getBhk());
        existing.setImageUrls(updatedProperty.getImageUrls());
        existing.setVideoUrl(updatedProperty.getVideoUrl());

        // Optional: when seller updates, send back to PENDING
        existing.setStatus(PropertyStatus.PENDING);

        return propertyRepository.save(existing);
    }

    @Override
    public void deleteProperty(String propertyId, String sellerId) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("You can delete only your own properties");
        }

        propertyRepository.delete(existing);
    }

    // ----------------- ADMIN -----------------

    @Override
    public List<Property> getPendingProperties() {
        return propertyRepository.findByStatus(PropertyStatus.PENDING);
    }

    @Override
    public Property approveProperty(String propertyId) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        existing.setStatus(PropertyStatus.APPROVED);
        return propertyRepository.save(existing);
    }

    @Override
    public List<Property> approveMultipleProperties(List<String> propertyIds) {
        List<Property> properties = propertyRepository.findAllById(propertyIds);
        for (Property p : properties) {
            p.setStatus(PropertyStatus.APPROVED);
        }
        return propertyRepository.saveAll(properties);
    }

    @Override
    public Property changeStatus(String propertyId, PropertyStatus status) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        existing.setStatus(status);
        return propertyRepository.save(existing);
    }

    // ----------------- SEARCH (Buyer-facing, APPROVED only) -----------------

    @Override
    public Page<Property> searchProperties(
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
    ) {

        // Default sort field
        String sortField = (sortBy == null || sortBy.isBlank()) ? "price" : sortBy;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        List<Criteria> criteriaList = new ArrayList<>();

        // Only show APPROVED properties to buyer
        criteriaList.add(Criteria.where("status").is(PropertyStatus.APPROVED));

        if (city != null && !city.isBlank()) {
            // case-insensitive exact match
            criteriaList.add(Criteria.where("city")
                    .regex("^" + Pattern.quote(city) + "$", "i"));
        }

        if (locality != null && !locality.isBlank()) {
            criteriaList.add(Criteria.where("locality")
                    .regex("^" + Pattern.quote(locality) + "$", "i"));
        }

        if (propertyType != null && !propertyType.isBlank()) {
            criteriaList.add(Criteria.where("propertyType").is(propertyType));
        }

        if (bhk != null) {
            criteriaList.add(Criteria.where("bhk").is(bhk));
        }

        if (listingType != null && !listingType.isBlank()) {
            criteriaList.add(Criteria.where("listingType").is(listingType));
        }

        if (minPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice));
        }

        if (maxPrice != null) {
            criteriaList.add(Criteria.where("price").lte(maxPrice));
        }

        if (minSize != null) {
            criteriaList.add(Criteria.where("sizeSqFt").gte(minSize));
        }

        if (maxSize != null) {
            criteriaList.add(Criteria.where("sizeSqFt").lte(maxSize));
        }

        Criteria criteria;
        if (criteriaList.isEmpty()) {
            criteria = new Criteria();
        } else {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria).with(pageable);

        List<Property> results = mongoTemplate.find(query, Property.class);
        long total = mongoTemplate.count(new Query(criteria), Property.class);

        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public String uploadPropertyImage(MultipartFile file) {

        try {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            Files.write(filePath, file.getBytes());

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed");
        }
    }

    @Override
    public Property getPropertyById(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

}
