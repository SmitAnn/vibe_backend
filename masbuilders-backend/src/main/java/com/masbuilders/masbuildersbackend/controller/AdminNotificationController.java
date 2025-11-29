package com.masbuilders.masbuildersbackend.controller;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@CrossOrigin
public class AdminNotificationController {

    private final NotificationService service;

    public AdminNotificationController(NotificationService service) {
        this.service = service;
    }

    // ✅ Get all notifications (Admin Dashboard)
    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    // ✅ Send notification to any user
    @PostMapping("/send")
    public Notification send(@RequestBody Map<String, String> body) {
        return service.sendNotification(
                body.get("userId"),
                body.get("title"),
                body.get("message")
        );
    }
}
