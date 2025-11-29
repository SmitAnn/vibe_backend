package com.masbuilders.masbuildersbackend.dto;


import lombok.Data;

import java.util.List;

@Data
public class PropertyDTO {
    private String title;
    private String description;
    private String city;
    private String address;
    private double price;
    private double sizeSqFt;
    private String propertyType;  // RENT / LEASE / SALE
    private List<String> imageUrls;
    private String videoUrl;
}

