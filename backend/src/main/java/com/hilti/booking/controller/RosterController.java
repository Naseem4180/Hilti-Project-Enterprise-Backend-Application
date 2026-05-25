package com.hilti.booking.controller;

import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.FELeave;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.FELeaveRepository;
import com.hilti.booking.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RosterController {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final FELeaveRepository feLeaveRepository;

    public RosterController(UserRepository userRepository,
                            BookingRepository bookingRepository,
                            FELeaveRepository feLeaveRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.feLeaveRepository = feLeaveRepository;
    }

    @GetMapping("/roster")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<List<Map<String, Object>>> getRoster(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<User> fieldExecutives = userRepository.findByRole(Role.ROLE_FE);
        List<Map<String, Object>> roster = new ArrayList<>();

        for (User fe : fieldExecutives) {
            List<Booking> bookings = bookingRepository.findByFieldExecutiveAndSlotDate(fe, date);
            List<FELeave> leaves = feLeaveRepository.findByFieldExecutive(fe).stream()
                    .filter(leave -> leave.getLeaveDate().equals(date))
                    .collect(Collectors.toList());

            Map<String, Object> entry = new HashMap<>();
            entry.put("feId", fe.getId());
            entry.put("feName", fe.getFullName());
            entry.put("email", fe.getEmail());
            entry.put("assignedBookings", bookings.stream().map(Booking::getBookingNumber).collect(Collectors.toList()));
            entry.put("bookingCount", bookings.size());
            entry.put("leaves", leaves.stream().map(FELeave::getLeaveDate).collect(Collectors.toList()));
            roster.add(entry);
        }
        return ResponseEntity.ok(roster);
    }
}
