package com.masbuilders.masbuildersbackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "favorites")
public class Favorite {
    @Id
    private String id;
    private String buyerEmail;
    private String propertyId;
    private LocalDateTime createdAt;
}
