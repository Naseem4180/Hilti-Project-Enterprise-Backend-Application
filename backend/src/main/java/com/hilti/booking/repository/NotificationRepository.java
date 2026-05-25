package com.hilti.booking.repository;

import com.hilti.booking.entity.Notification;
import com.hilti.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
