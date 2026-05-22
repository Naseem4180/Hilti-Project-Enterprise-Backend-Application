package com.hilti.booking.controller;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public AdminController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        long totalBookings = bookingRepository.count();
        long cancelledBookings = bookingRepository.findByStatus(com.hilti.booking.entity.BookingStatus.CANCELLED).size();
        return ResponseEntity.ok(Map.of(
                "totalBookings", totalBookings,
                "cancelledBookings", cancelledBookings
        ));
    }
}
