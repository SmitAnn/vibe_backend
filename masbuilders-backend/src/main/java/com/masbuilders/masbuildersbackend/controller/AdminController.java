package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.enums.PropertyStatus;
import com.masbuilders.masbuildersbackend.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminController {

    private final PropertyService propertyService;

    public AdminController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // ✅ View all PENDING properties
    @GetMapping("/pending")
    public List<Property> getPendingProperties() {
        return propertyService.getPendingProperties();
    }

    // ✅ Approve single property
    @PutMapping("/approve/{propertyId}")
    public Property approveProperty(@PathVariable String propertyId) {
        return propertyService.approveProperty(propertyId);
    }

    // ✅ Bulk approve properties
    @PutMapping("/approve-multiple")
    public List<Property> approveMultiple(@RequestBody List<String> propertyIds) {
        return propertyService.approveMultipleProperties(propertyIds);
    }

    // ✅ Change property status (approve / reject)
    @PutMapping("/change-status/{propertyId}/{status}")
    public Property changeStatus(@PathVariable String propertyId,
                                 @PathVariable PropertyStatus status) {
        return propertyService.changeStatus(propertyId, status);
    }

    // ✅ NEW: Analytics endpoint for Admin Dashboard
    @GetMapping("/analytics")
    public Map<String, Object> getAnalytics() {
        Map<String, Object> stats = new HashMap<>();

        long totalProperties = propertyService.countAll();
        long approved = propertyService.countByStatus(PropertyStatus.APPROVED);
        long pending = propertyService.countByStatus(PropertyStatus.PENDING);
        long rejected = propertyService.countByStatus(PropertyStatus.REJECTED);

        long totalBuyers = propertyService.countUsersByRole("BUYER");
        long totalSellers = propertyService.countUsersByRole("SELLER");

        long totalFavorites = propertyService.countTotalFavorites();

        stats.put("totalProperties", totalProperties);
        stats.put("approvedProperties", approved);
        stats.put("pendingProperties", pending);
        stats.put("rejectedProperties", rejected);
        stats.put("totalBuyers", totalBuyers);
        stats.put("totalSellers", totalSellers);
        stats.put("totalFavorites", totalFavorites);

        return stats;
    }
}
