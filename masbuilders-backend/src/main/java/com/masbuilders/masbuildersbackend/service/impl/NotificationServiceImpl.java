package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.entity.Property;
import com.masbuilders.masbuildersbackend.entity.User;
import com.masbuilders.masbuildersbackend.repository.NotificationRepository;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.repository.UserRepository;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   PropertyRepository propertyRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification sendNotification(String userId, String title, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    @Override
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification markAsRead(String id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        return notificationRepository.save(n);
    }

    @Override
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    /**
     * Notify the seller and admin when a buyer favorites a property.
     */
    @Override
    public void notifySellerOnFavorite(String propertyId, String buyerEmail) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null || property.getSellerId() == null) return;

        String sellerId = property.getSellerId();
        String title = "💖 New Favorite!";
        String message = String.format("Your property '%s' was favorited by %s.", property.getTitle(), buyerEmail);

        // Send to seller
        sendNotification(sellerId, title, message);

        // Also notify all admins
        String adminMsg = String.format("%s favorited property '%s' (ID: %s)", buyerEmail, property.getTitle(), propertyId);
        notifyAdmin("📢 Buyer Liked Property", adminMsg);
    }

    /**
     * Notify all admin users in the system.
     */
    @Override
    public void notifyAdmin(String title, String message) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase("ADMIN"))
                .toList();

        for (User admin : admins) {
            sendNotification(admin.getId(), title, message);
        }
    }
}
