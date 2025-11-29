package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // ----------------- BUYER (ALL APPROVED) -----------------

    @GetMapping("/approved")
    public List<Property> getApprovedProperties() {
        return propertyService.getAllApprovedProperties();
    }

    // ----------------- SEARCH + FILTER + PAGINATION -----------------

    @GetMapping("/search")
    public Page<Property> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) Double maxSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return propertyService.searchProperties(
                city, locality, propertyType, bhk,
                listingType, minPrice, maxPrice,
                minSize, maxSize, page, size, sortBy, sortDir
        );
    }

    // ----------------- SELLER -----------------

    @PostMapping("/add/{sellerId}")
    public Property addProperty(@RequestBody Property property,
                                @PathVariable String sellerId) {
        return propertyService.addProperty(property, sellerId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Property> getSellerProperties(@PathVariable String sellerId) {
        return propertyService.getSellerProperties(sellerId);
    }

    @PutMapping("/update/{propertyId}/{sellerId}")
    public Property updateProperty(@PathVariable String propertyId,
                                   @PathVariable String sellerId,
                                   @RequestBody Property property) {
        return propertyService.updateProperty(propertyId, property, sellerId);
    }

    @DeleteMapping("/delete/{propertyId}/{sellerId}")
    public String deleteProperty(@PathVariable String propertyId,
                                 @PathVariable String sellerId) {
        propertyService.deleteProperty(propertyId, sellerId);
        return "Property Deleted Successfully";
    }

    // ----------------- ✅ IMAGE UPLOAD (SAVES TO uploads/images) -----------------

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = "uploads/images/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/images/" + fileName);
    }

    // -----------------  VIDEO UPLOAD (SAVES TO uploads/videos) -----------------

    @PostMapping("/upload/video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = "uploads/videos/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/videos/" + fileName);
    }
}
