package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.TestingReport;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReportController(
            BookingRepository bookingRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * CREATE REPORT
     */
    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_FE')")
    public ResponseEntity<?> createReport(
            @PathVariable("bookingId") Long bookingId,
            @Valid @RequestBody TestingReport report,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * VALIDATE ASSIGNED FE
         */
        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        /**
         * REPORT ALREADY EXISTS
         */
        if (booking.getTestingReport() != null) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Report already exists for this booking"
                    ));
        }

        /**
         * BOOKING STATUS VALIDATION
         */
        if (
                booking.getStatus() != BookingStatus.IN_PROGRESS &&
                booking.getStatus() != BookingStatus.CONFIRMED
        ) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Report cannot be created for this booking status"
                    ));
        }

        report.setBooking(booking);

        report.setCompletedAt(LocalDateTime.now());

        report.setSynced(true);

        booking.setTestingReport(report);

        booking.setStatus(BookingStatus.COMPLETED);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(report);
    }

    /**
     * GET REPORT BY BOOKING ID
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getReport(
            @PathVariable("bookingId") Long bookingId,
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

        TestingReport report = booking.getTestingReport();

        if (report == null) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Report not found"));
        }

        return ResponseEntity.ok(report);
    }

    /**
     * UPDATE REPORT
     */
    @PutMapping("/{bookingId}")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_FE')")
    public ResponseEntity<?> updateReport(
            @PathVariable("bookingId") Long bookingId,
            @Valid @RequestBody TestingReport updatedReport,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * VALIDATE FE
         */
        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        TestingReport existingReport = booking.getTestingReport();

        if (existingReport == null) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Report not found"));
        }

        /**
         * UPDATE REPORT FIELDS
         */
        existingReport.setReportData(updatedReport.getReportData());

        existingReport.setFeSignature(updatedReport.getFeSignature());

        existingReport.setCustomerSignature(
                updatedReport.getCustomerSignature()
        );

        existingReport.setCompletedAt(LocalDateTime.now());

        existingReport.setSynced(true);

        bookingRepository.save(booking);

        return ResponseEntity.ok(existingReport);
    }

    /**
     * DELETE REPORT
     */
    @DeleteMapping("/{bookingId}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> deleteReport(
            @PathVariable("bookingId") Long bookingId
    ) {

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getTestingReport() == null) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Report not found"));
        }

        booking.setTestingReport(null);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Report deleted successfully")
        );
    }

    /**
     * MARK REPORT AS OFFLINE SYNCED
     */
    @PostMapping("/{bookingId}/sync")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_FE')")
    public ResponseEntity<ApiResponse> syncOfflineReport(
            @PathVariable("bookingId") Long bookingId,
            Authentication authentication
    ) {

        User fe = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (
                booking.getFieldExecutive() == null ||
                !booking.getFieldExecutive().getId().equals(fe.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        if (booking.getTestingReport() == null) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Report not found"));
        }

        booking.getTestingReport().setSynced(true);

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Offline report synced successfully")
        );
    }
}