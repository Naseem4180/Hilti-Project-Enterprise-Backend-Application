package com.hilti.booking.repository;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.Feedback;
import com.hilti.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCustomer(User customer);
    List<Feedback> findByBooking(Booking booking);
    boolean existsByBooking(Booking booking);
}
