package com.hilti.booking.repository;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(User customer);
    List<Booking> findByFieldExecutive(User fieldExecutive);
    List<Booking> findByStatus(BookingStatus status);
}
