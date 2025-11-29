package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // Buyer / Seller / Admin: View own notifications
    @GetMapping("/my")
    public List<Notification> myNotifications(Principal principal) {
        return service.getUserNotifications(principal.getName());
    }

    // Mark as read
    @PutMapping("/read/{id}")
    public Notification markRead(@PathVariable String id) {
        return service.markAsRead(id);
    }

    //  Delete notification
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteNotification(id);
    }
}
