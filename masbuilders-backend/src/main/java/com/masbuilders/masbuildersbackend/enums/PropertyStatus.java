package com.masbuilders.masbuildersbackend.enums;


public enum PropertyStatus {
    PENDING,    // Property added by seller but not yet approved by admin
    APPROVED,   // Visible publicly after admin approval
    REJECTED    // Admin rejected the listing
}
