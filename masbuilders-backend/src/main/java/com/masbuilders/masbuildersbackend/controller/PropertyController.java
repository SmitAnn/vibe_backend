package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // ✅ Frontend access
public class PropertyController {

    private final PropertyService propertyService;
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // ----------------- ✅ GET PROPERTY BY ID -----------------
    // ✅ Fetch single property by ID (used in Edit + Details pages)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable String id) {
        try {
            Property property = propertyService.getPropertyById(id);
            if (property == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Property not found with ID: " + id));
            }
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch property", "details", e.getMessage()));
        }
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
    @PostMapping(value = "/add/{sellerId}", consumes = "multipart/form-data")
    public ResponseEntity<?> addProperty(
            @RequestPart("property") Property property,
            @PathVariable String sellerId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video) {

        logger.info("🟢 Received new property submission from seller: {}", sellerId);

        try {
            Files.createDirectories(Paths.get("uploads/images"));
            Files.createDirectories(Paths.get("uploads/videos"));

            List<String> imagePaths = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get("uploads/images/" + fileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/uploads/images/" + fileName);
                }
            }

            String videoUrl = null;
            if (video != null && !video.isEmpty()) {
                String videoName = UUID.randomUUID() + "_" + video.getOriginalFilename();
                Path videoPath = Paths.get("uploads/videos/" + videoName);
                Files.copy(video.getInputStream(), videoPath, StandardCopyOption.REPLACE_EXISTING);
                videoUrl = "/uploads/videos/" + videoName;
            }

            property.setSellerId(sellerId);
            property.setImageUrls(imagePaths);
            property.setVideoUrl(videoUrl);
            property.setStatus(com.masbuilders.masbuildersbackend.enums.PropertyStatus.PENDING);
            property.setCreatedAt(Instant.now());

            Property saved = propertyService.addProperty(property, sellerId);
            logger.info("✅ Property '{}' added successfully for seller: {}", saved.getTitle(), sellerId);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            logger.error("❌ IO Error while saving property for seller {}: {}", sellerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error saving property files: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error while adding property for seller {}: {}", sellerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error saving property: " + e.getMessage()));
        }
    }

    @GetMapping("/seller/{sellerId}")
    public List<Property> getSellerProperties(@PathVariable String sellerId) {
        return propertyService.getSellerProperties(sellerId);
    }

    // ✅ Update Property - JSON Only
    @PutMapping("/update/{propertyId}/{sellerId}")
    public ResponseEntity<?> updateProperty(
            @PathVariable String propertyId,
            @PathVariable String sellerId,
            @RequestBody Property property) {
        try {
            Property updated = propertyService.updateProperty(propertyId, property, sellerId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("❌ Error updating property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Update Property - Multipart (images/videos)
    @PostMapping(value = "/update/{propertyId}/{sellerId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePropertyWithMedia(
            @PathVariable String propertyId,
            @PathVariable String sellerId,
            @RequestPart("property") Property property,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video) {

        try {
            Files.createDirectories(Paths.get("uploads/images"));
            Files.createDirectories(Paths.get("uploads/videos"));

            if (images != null && !images.isEmpty()) {
                List<String> imagePaths = new ArrayList<>();
                for (MultipartFile image : images) {
                    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get("uploads/images/" + fileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/uploads/images/" + fileName);
                }
                property.setImageUrls(imagePaths);
            }

            if (video != null && !video.isEmpty()) {
                String videoName = UUID.randomUUID() + "_" + video.getOriginalFilename();
                Path videoPath = Paths.get("uploads/videos/" + videoName);
                Files.copy(video.getInputStream(), videoPath, StandardCopyOption.REPLACE_EXISTING);
                property.setVideoUrl("/uploads/videos/" + videoName);
            }

            property.setSellerId(sellerId);
            property.setCreatedAt(Instant.now());

            Property updated = propertyService.updateProperty(propertyId, property, sellerId);
            return ResponseEntity.ok(updated);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error updating property files: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error updating property: " + e.getMessage()));
        }
    }

    // ----------------- DELETE -----------------
    @DeleteMapping("/delete/{propertyId}/{sellerId}")
    public ResponseEntity<?> deleteProperty(@PathVariable String propertyId,
                                            @PathVariable String sellerId) {
        try {
            propertyService.deleteProperty(propertyId, sellerId);
            return ResponseEntity.ok(Map.of("message", "Property deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ----------------- UPLOAD IMAGE -----------------
    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/images/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/images/" + fileName);
    }

    // ----------------- UPLOAD VIDEO -----------------
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
