package com.masbuilders.masbuildersbackend.service.impl;

import com.masbuilders.masbuildersbackend.entity.Notification;
import com.masbuilders.masbuildersbackend.repository.NotificationRepository;
import com.masbuilders.masbuildersbackend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification sendNotification(String userId, String title, String message) {
        Notification n = new Notification();
        n.setUserId(userId);        // ✅ Matches entity
        n.setTitle(title);
        n.setMessage(message);
        return repository.save(n);
    }

    @Override
    public List<Notification> getUserNotifications(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    @Override
    public Notification markAsRead(String id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        return repository.save(n);
    }

    @Override
    public void deleteNotification(String id) {
        repository.deleteById(id);
    }
}
