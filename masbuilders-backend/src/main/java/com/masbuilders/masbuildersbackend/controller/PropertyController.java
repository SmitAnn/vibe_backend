package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PropertyController {

    private final PropertyService propertyService;
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // ----------------- BUYER (ALL APPROVED) -----------------
    @GetMapping("/approved")
    public List<Property> getApprovedProperties() {
        return propertyService.getAllApprovedProperties();
    }

    // ✅ FIX: Get Property by ID (for details page)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable String id) {
        try {
            Property property = propertyService.getPropertyById(id);
            return property != null
                    ? ResponseEntity.ok(property)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Property not found with ID: " + id);
        } catch (Exception e) {
            logger.error("❌ Error fetching property {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching property details");
        }
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

            // ✅ Handle image uploads
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get("uploads/images/" + fileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/uploads/images/" + fileName);
                }
            }

            // ✅ Handle video upload
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
                    .body("Error saving property files: " + e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Unexpected error while adding property for seller {}: {}", sellerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving property: " + e.getMessage());
        }
    }

    @GetMapping("/seller/{sellerId}")
    public List<Property> getSellerProperties(@PathVariable String sellerId) {
        return propertyService.getSellerProperties(sellerId);
    }

    // ✅ Update Property - JSON Only
    @PutMapping("/update/{propertyId}/{sellerId}")
    public Property updateProperty(
            @PathVariable String propertyId,
            @PathVariable String sellerId,
            @RequestBody Property property) {
        return propertyService.updateProperty(propertyId, property, sellerId);
    }

    // ✅ Update Property - Multipart (with new images/videos)
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

            // Handle image uploads
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

            // Handle video upload
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
            return ResponseEntity.status(500).body("Error updating property files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating property: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{propertyId}/{sellerId}")
    public String deleteProperty(@PathVariable String propertyId,
                                 @PathVariable String sellerId) {
        propertyService.deleteProperty(propertyId, sellerId);
        return "Property Deleted Successfully";
    }

    // ----------------- ✅ IMAGE UPLOAD -----------------
    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/images/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/images/" + fileName);
    }

    // ----------------- ✅ VIDEO UPLOAD -----------------
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
