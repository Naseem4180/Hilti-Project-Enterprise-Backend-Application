package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.dto.BookingRequest;
import com.hilti.booking.entity.*;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        String email = authentication.getName();
        User customer = userRepository.findByEmail(email).orElseThrow();

        Booking booking = new Booking();
        booking.setBookingNumber("HB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setSoNumber(request.getSoNumber());
        booking.setSapAccountId(request.getSapAccountId());
        booking.setCompanyName(request.getCompanyName());
        booking.setAddress(request.getAddress());
        booking.setPostalCode(request.getPostalCode());
        booking.setBookingType(BookingType.valueOf(request.getBookingType()));
        booking.setSlotType(SlotType.valueOf(request.getSlotType()));
        booking.setTestingType(TestingType.valueOf(request.getTestingType()));
        booking.setSlotDateTimeStart(request.getSlotDateTimeStart());
        booking.setSlotDateTimeEnd(request.getSlotDateTimeEnd());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCustomer(customer);

        if (request.getAnchorDetail() != null) {
            AnchorDetail detail = new AnchorDetail();
            detail.setAnchorCategory(request.getAnchorDetail().getAnchorCategory());
            detail.setAnchorType(request.getAnchorDetail().getAnchorType());
            detail.setAnchorSize(request.getAnchorDetail().getAnchorSize());
            detail.setAnchorQuantity(request.getAnchorDetail().getAnchorQuantity());
            detail.setProofLoadValue(request.getAnchorDetail().getProofLoadValue());
            booking.setAnchorDetail(detail);
        }

        if (request.getOnsiteContacts() != null) {
            booking.setOnsiteContacts(request.getOnsiteContacts().stream().map(item -> {
                OnsiteContact contact = new OnsiteContact();
                contact.setName(item.getName());
                contact.setContactNumber(item.getContactNumber());
                contact.setEmail(item.getEmail());
                contact.setBooking(booking);
                return contact;
            }).collect(Collectors.toList()));
        }

        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> listMyBookings(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            return ResponseEntity.ok(bookingRepository.findByCustomer(user));
        }
        if (user.getRole() == Role.ROLE_FE) {
            return ResponseEntity.ok(bookingRepository.findByFieldExecutive(user));
        }
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long id, @RequestParam String cancelledBy, @RequestParam String cancelType) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        Cancellation cancellation = new Cancellation();
        cancellation.setBooking(booking);
        cancellation.setCancelledBy(cancelledBy);
        cancellation.setCancelType(CancelType.valueOf(cancelType));
        cancellation.setReason("User requested cancellation");
        booking.getCancellations().add(cancellation);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled."));
    }
}
