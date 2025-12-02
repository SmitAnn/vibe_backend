package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import com.masbuilders.masbuildersbackend.repository.FavoriteRepository;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.repository.UserRepository;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               UserRepository userRepository,
                               FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    // ==========================
    // 💡 Buyer Methods
    // ==========================
    @Override
    public List<Property> getAllApprovedProperties() {
        return propertyRepository.findByStatus(PropertyStatus.APPROVED);
    }

    // ==========================
    // 🏠 Seller Methods
    // ==========================
    @Override
    public Property addProperty(Property property, String sellerId) {
        property.setSellerId(sellerId);
        property.setStatus(PropertyStatus.PENDING);
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getSellerProperties(String sellerId) {
        return propertyRepository.findBySellerId(sellerId);
    }

    @Override
    public Property updateProperty(String propertyId, Property property, String sellerId) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not authorized to edit this property");
        }

        property.setId(propertyId);
        property.setSellerId(sellerId);
        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(String propertyId, String sellerId) {
        Property existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not authorized to delete this property");
        }

        propertyRepository.delete(existing);
    }

    // ==========================
    // 🧑‍💼 Admin Methods
    // ==========================
    @Override
    public List<Property> getPendingProperties() {
        return propertyRepository.findByStatus(PropertyStatus.PENDING);
    }

    @Override
    public Property approveProperty(String propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(PropertyStatus.APPROVED);
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> approveMultipleProperties(List<String> propertyIds) {
        List<Property> properties = propertyRepository.findAllById(propertyIds);
        properties.forEach(p -> p.setStatus(PropertyStatus.APPROVED));
        return propertyRepository.saveAll(properties);
    }

    @Override
    public Property changeStatus(String propertyId, PropertyStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(status);
        return propertyRepository.save(property);
    }

    @Override
    public Property getPropertyById(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    // ==========================
    // 🔍 Search (for filters)
    // ==========================
    @Override
    public Page<Property> searchProperties(String city,
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
                                           String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return propertyRepository.findAll(pageable);
    }

    // ==========================
    // 📸 Image Upload Stub
    // ==========================
    @Override
    public String uploadPropertyImage(MultipartFile file) {
        return "/uploads/" + file.getOriginalFilename();
    }

    // ==========================
    // 📊 Analytics Methods
    // ==========================
    @Override
    public long countAll() {
        return propertyRepository.count();
    }

    @Override
    public long countByStatus(PropertyStatus status) {
        return propertyRepository.findByStatus(status).size();
    }

    @Override
    public long countUsersByRole(String role) {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(role))
                .count();
    }

    @Override
    public long countTotalFavorites() {
        return favoriteRepository.count();
    }
}
