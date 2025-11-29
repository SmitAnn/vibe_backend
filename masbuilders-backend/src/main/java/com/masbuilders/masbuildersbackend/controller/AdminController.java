package com.masbuilders.masbuildersbackend.controller;


import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final PropertyService propertyService;

    public AdminController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // View all PENDING properties
    @GetMapping("/pending")
    public List<Property> getPendingProperties() {
        return propertyService.getPendingProperties();
    }

    // Approve single property
    @PutMapping("/approve/{propertyId}")
    public Property approveProperty(@PathVariable String propertyId) {
        return propertyService.approveProperty(propertyId);
    }

    // Bulk approve
    @PutMapping("/approve-multiple")
    public List<Property> approveMultiple(@RequestBody List<String> propertyIds) {
        return propertyService.approveMultipleProperties(propertyIds);
    }

    // Reject / Change status
    @PutMapping("/change-status/{propertyId}/{status}")
    public Property changeStatus(@PathVariable String propertyId,
                                 @PathVariable PropertyStatus status) {
        return propertyService.changeStatus(propertyId, status);
    }
}
