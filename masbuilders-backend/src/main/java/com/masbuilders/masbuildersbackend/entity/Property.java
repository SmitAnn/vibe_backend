package com.masbuilders.masbuildersbackend.entity;

import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "properties")
public class Property {

    @Id
    private String id;

    // Only seller can own the property
    private String sellerId;

    private String title;
    private String description;

    private String propertyType;
    private Integer bhk;
    private String listingType;

    private String city;
    private String locality;   // e.g. Whitefield, OMR, etc.
    private String address;

    private Double price;
    private Double sizeSqFt;

    private List<String> imageUrls;
    private String videoUrl;

    // 🌍 NEW FIELDS: Coordinates for Map View
    private Double latitude;   // e.g. 12.9716
    private Double longitude;  // e.g. 77.5946

    // PENDING / APPROVED / REJECTED
    private PropertyStatus status = PropertyStatus.PENDING;

    // For sorting by newest
    private Instant createdAt = Instant.now();
}
