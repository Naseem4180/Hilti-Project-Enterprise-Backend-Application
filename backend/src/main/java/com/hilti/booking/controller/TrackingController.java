package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/track")
public class TrackingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public TrackingController(
            BookingRepository bookingRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * TRACK BOOKING
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> trackBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * CUSTOMER SECURITY
         */
        if (user.getRole() == Role.ROLE_CUSTOMER) {

            if (
                    booking.getCustomer() == null ||
                    !booking.getCustomer().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        /**
         * FE SECURITY
         */
        if (user.getRole() == Role.ROLE_FE) {

            if (
                    booking.getFieldExecutive() == null ||
                    !booking.getFieldExecutive().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        String fieldExecutiveName =
                booking.getFieldExecutive() != null
                        ? booking.getFieldExecutive().getFullName()
                        : "Unassigned";

        String currentLocation = "Not started";

        Integer etaMinutes = null;

        /**
         * TRACKING STATUS LOGIC
         */
        switch (booking.getStatus()) {

            case CONFIRMED -> {
                currentLocation = "Booking confirmed";
                etaMinutes = 60;
            }

            case IN_PROGRESS -> {
                currentLocation = "Travelling to customer site";
                etaMinutes = 15;
            }

            case COMPLETED -> {
                currentLocation = "Testing completed";
                etaMinutes = 0;
            }

            case CANCELLED -> {
                currentLocation = "Booking cancelled";
                etaMinutes = null;
            }

            case POSTPONED -> {
                currentLocation = "Booking postponed";
                etaMinutes = null;
            }

            default -> {
                currentLocation = "Pending";
                etaMinutes = null;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", booking.getId());
        response.put("status", booking.getStatus().name());
        response.put("fieldExecutive", fieldExecutiveName);
        response.put("etaMinutes", etaMinutes);
        response.put("currentLocation", currentLocation);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE FE LIVE LOCATION
     */
    @PostMapping("/{bookingId}/location")
    @Transactional
    public ResponseEntity<?> updateLocation(
            @PathVariable Long bookingId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Field Executive not found")
                );

        if (fe.getRole() != Role.ROLE_FE) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found")
                );

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Not assigned to booking"));
        }

        /**
         * SAVE LOCATION
         * ADD THESE FIELDS IN BOOKING ENTITY:
         *
         * private Double currentLatitude;
         * private Double currentLongitude;
         * private LocalDateTime lastLocationUpdatedAt;
         */

        booking.setCurrentLatitude(latitude);

        booking.setCurrentLongitude(longitude);

        booking.setLastLocationUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Location updated successfully")
        );
    }

    /**
     * GET LIVE TRACKING DETAILS
     */
    @GetMapping("/{bookingId}/live")
    public ResponseEntity<?> getLiveTracking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found")
                );

        /**
         * CUSTOMER ACCESS VALIDATION
         */
        if (user.getRole() == Role.ROLE_CUSTOMER) {

            if (
                    booking.getCustomer() == null ||
                    !booking.getCustomer().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        Map<String, Object> response = new HashMap<>();

        response.put("bookingId", booking.getId());

        response.put("bookingNumber", booking.getBookingNumber());

        response.put(
                "fieldExecutive",
                booking.getFieldExecutive() != null
                        ? booking.getFieldExecutive().getFullName()
                        : null
        );

        response.put("status", booking.getStatus());

        response.put("latitude", booking.getCurrentLatitude());

        response.put("longitude", booking.getCurrentLongitude());

        response.put(
                "lastUpdatedAt",
                booking.getLastLocationUpdatedAt()
        );

        response.put(
                "slotDate",
                booking.getSlotDate()
        );

        response.put(
                "slotStartTime",
                booking.getSlotTimeStart()
        );

        response.put(
                "slotEndTime",
                booking.getSlotTimeEnd()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * CUSTOMER START TESTING
     */
    @PostMapping("/{bookingId}/testing-start")
    @Transactional
    public ResponseEntity<?> startTesting(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        User customer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found")
                );

        if (customer.getRole() != Role.ROLE_CUSTOMER) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found")
                );

        if (
                booking.getCustomer() == null ||
                !booking.getCustomer().getId().equals(customer.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Booking cannot start"
                    ));
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Testing started successfully")
        );
    }
}