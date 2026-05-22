package com.hilti.booking.controller;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.TestingReport;
import com.hilti.booking.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final BookingRepository bookingRepository;

    public ReportController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReport(@RequestParam Long bookingId, @RequestBody TestingReport report) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        report.setBooking(booking);
        report.setCompletedAt(LocalDateTime.now());
        booking.setTestingReport(report);
        bookingRepository.save(booking);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<TestingReport> getReport(@PathVariable Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(Booking::getTestingReport)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
