package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.FELeave;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.FELeaveRepository;
import com.hilti.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fe")
@PreAuthorize("hasAuthority('ROLE_FE')")
public class FEController {

    private final FELeaveRepository feLeaveRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public FEController(
            FELeaveRepository feLeaveRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository
    ) {
        this.feLeaveRepository = feLeaveRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * GET FE ASSIGNMENTS
     */
    @GetMapping("/assignments")
    public ResponseEntity<List<Booking>> getAssignments(
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        List<Booking> assignments =
                bookingRepository.findByFieldExecutive(fe);

        return ResponseEntity.ok(assignments);
    }

    /**
     * GET ASSIGNMENT BY ID
     */
    @GetMapping("/assignments/{id}")
    public ResponseEntity<?> getAssignmentById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        return ResponseEntity.ok(booking);
    }

    /**
     * SUBMIT LEAVE
     */
    @PostMapping("/leave")
    @Transactional
    public ResponseEntity<?> submitLeave(
            Authentication authentication,
            @RequestParam String date
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        LocalDate leaveDate = LocalDate.parse(date);

        boolean alreadyExists = feLeaveRepository
                .findByFieldExecutive(fe)
                .stream()
                .anyMatch(item -> item.getLeaveDate().equals(leaveDate));

        if (alreadyExists) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Leave already submitted for this date"));
        }

        FELeave leave = new FELeave();

        leave.setFieldExecutive(fe);

        leave.setLeaveDate(leaveDate);

        leave.setApproved(false);

        leave.setCreatedAt(LocalDateTime.now());

        FELeave savedLeave = feLeaveRepository.save(leave);

        return ResponseEntity.ok(savedLeave);
    }

    /**
     * GET MY LEAVES
     */
    @GetMapping("/leave")
    public ResponseEntity<List<FELeave>> getMyLeaves(
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        return ResponseEntity.ok(
                feLeaveRepository.findByFieldExecutive(fe)
        );
    }

    /**
     * UPDATE ASSIGNMENT STATUS
     */
    @PutMapping("/assignments/{id}/status")
    @Transactional
    public ResponseEntity<ApiResponse> updateAssignmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));
        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        BookingStatus bookingStatus;

        try {

            bookingStatus =
                    BookingStatus.valueOf(status.toUpperCase());

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid booking status"));
        }

        /**
         * STATUS FLOW VALIDATION
         */
        if (
                booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED
        ) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Booking already closed"));
        }

        booking.setStatus(bookingStatus);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Booking status updated")
        );
    }

    /**
     * ACCEPT ASSIGNMENT
     */
    @PostMapping("/assignments/{id}/accept")
    @Transactional
    public ResponseEntity<ApiResponse> acceptAssignment(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Booking cannot be accepted"));
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Assignment accepted")
        );
    }

    /**
     * COMPLETE BOOKING
     */
    @PostMapping("/assignments/{id}/complete")
    @Transactional
    public ResponseEntity<ApiResponse> completeBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Booking is not in progress"));
        }

        booking.setStatus(BookingStatus.COMPLETED);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Booking completed successfully")
        );
    }
}