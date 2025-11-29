package com.masbuilders.masbuildersbackend.dto;


import lombok.Data;

@Data
public class NotificationDTO {
    private String id;
    private String message;
    private boolean read;
    private String createdAt;
}
