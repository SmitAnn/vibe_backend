package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    /**
     * ✅ Get notifications for the currently logged-in user (extracted from JWT)
     */
    @GetMapping("/my")
    public List<Notification> getUserNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User not authenticated or token missing.");
        }

        String userId = auth.getName(); // typically the email from JWT's 'sub' claim
        System.out.println("📩 Fetching notifications for: " + userId);

        List<Notification> notifications = service.getUserNotifications(userId);
        System.out.println("✅ Found " + notifications.size() + " notifications for " + userId);

        return notifications;
    }

    /**
     * ✅ Mark a specific notification as read
     */
    @PutMapping("/read/{id}")
    public Notification markRead(@PathVariable String id) {
        return service.markAsRead(id);
    }

    /**
     * ✅ Delete a specific notification
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteNotification(id);
    }

    /**
     * ✅ Admin-only: fetch all notifications
     */
    @GetMapping
    public List<Notification> getAllNotifications() {
        return service.getAllNotifications();
    }
}
