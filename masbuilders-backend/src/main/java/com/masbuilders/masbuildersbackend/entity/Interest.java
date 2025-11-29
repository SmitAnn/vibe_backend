package com.masbuilders.masbuildersbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "interests")
public class Interest {

    @Id
    private String id;

    private String propertyId;
    private String buyerId;
    private String sellerId;
    private boolean viewedBySeller = false;
}
