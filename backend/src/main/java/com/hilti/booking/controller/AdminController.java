package com.hilti.booking.controller;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
public class AdminController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public AdminController(
            BookingRepository bookingRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Analytics dashboard
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {

        long totalBookings = bookingRepository.count();

        long completedBookings =
                bookingRepository.findByStatus(BookingStatus.COMPLETED).size();

        long cancelledBookings =
                bookingRepository.findByStatus(BookingStatus.CANCELLED).size();

        long confirmedBookings =
                bookingRepository.findByStatus(BookingStatus.CONFIRMED).size();

        return ResponseEntity.ok(
                Map.of(
                        "totalBookings", totalBookings,
                        "completedBookings", completedBookings,
                        "cancelledBookings", cancelledBookings,
                        "confirmedBookings", confirmedBookings
                )
        );
    }

    /**
     * Reassign FE
     */
    @PutMapping("/assignments/{id}/reassign")
    @Transactional
    public ResponseEntity<?> reassignFieldExecutive(
            @PathVariable Long id,
            @RequestParam String feEmail
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User fe = userRepository.findByEmail(feEmail)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (fe.getRole() != Role.ROLE_FE) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Selected user is not a Field Executive"
                    )
            );
        }

        booking.setFieldExecutive(fe);

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Field Executive reassigned successfully"
                )
        );
    }

    /**
     * Archived bookings
     */
    @GetMapping("/archived-data")
    public ResponseEntity<List<Booking>> getArchivedData() {

        List<Booking> archivedBookings =
                bookingRepository.findByStatusIn(
                        List.of(
                                BookingStatus.CANCELLED,
                                BookingStatus.COMPLETED
                        )
                );

        return ResponseEntity.ok(archivedBookings);
    }
}