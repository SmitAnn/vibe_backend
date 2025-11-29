package com.masbuilders.masbuildersbackend.dto;


import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import lombok.Data;

import java.util.List;

@Data
public class PropertyResponseDTO {
    private String id;
    private String sellerId;
    private String title;
    private String description;
    private String city;
    private String address;
    private double price;
    private double sizeSqFt;
    private String propertyType;
    private List<String> imageUrls;
    private String videoUrl;
    private PropertyStatus status;
}


