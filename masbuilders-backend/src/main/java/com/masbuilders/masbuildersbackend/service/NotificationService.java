package com.masbuilders.masbuildersbackend.service;

import com.masbuilders.masbuildersbackend.entity.Notification;
import java.util.List;

public interface NotificationService {

    Notification sendNotification(String userId, String title, String message);

    List<Notification> getUserNotifications(String userId);

    List<Notification> getAllNotifications();

    Notification markAsRead(String id);

    void deleteNotification(String id);
}
