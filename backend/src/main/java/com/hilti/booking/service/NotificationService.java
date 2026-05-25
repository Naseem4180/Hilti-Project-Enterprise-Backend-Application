package com.hilti.booking.service;

import com.hilti.booking.entity.Notification;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public Notification notify(User user, String type, String message, String channel) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setChannel(channel);
        Notification saved = notificationRepository.save(notification);
        if (user.getEmail() != null) {
            emailService.sendEmail(user.getEmail(), "Hilti Booking Notification", message);
        }
        return saved;
    }
}
