package com.hilti.booking.repository;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * CUSTOMER BOOKINGS
     */
    List<Booking> findByCustomer(User customer);

    /**
     * FE BOOKINGS
     */
    List<Booking> findByFieldExecutive(User fieldExecutive);

    /**
     * STATUS FILTER
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * MULTIPLE STATUS FILTER
     */
    List<Booking> findByStatusIn(
            List<BookingStatus> statuses
    );

    /**
     * ROSTER FILTER
     */
    List<Booking> findByFieldExecutiveAndSlotDate(
            User fieldExecutive,
            LocalDate slotDate
    );
}