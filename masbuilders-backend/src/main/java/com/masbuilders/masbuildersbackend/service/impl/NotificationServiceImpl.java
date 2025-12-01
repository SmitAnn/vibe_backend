package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.repository.NotificationRepository;
import com.masbuilders.masbuildersbackend.repository.PropertyRepository;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PropertyRepository propertyRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   PropertyRepository propertyRepository) {
        this.notificationRepository = notificationRepository;
        this.propertyRepository = propertyRepository;
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
        Optional<Notification> n = notificationRepository.findById(id);
        if (n.isPresent()) {
            Notification notif = n.get();
            notif.setRead(true);
            return notificationRepository.save(notif);
        }
        return null;
    }

    @Override
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public Notification notifySellerOnFavorite(String propertyId, String buyerEmail) {
        return propertyRepository.findById(propertyId)
                .map(property -> {
                    String sellerId = property.getSellerId();
                    String title = "Your property got a new favorite ❤️";
                    String message = buyerEmail + " favorited your property: " + property.getTitle();
                    return sendNotification(sellerId, title, message);
                })
                .orElse(null);
    }
}
