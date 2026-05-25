package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.dto.BookingRequest;
import com.hilti.booking.entity.*;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.SlotRepository;
import com.hilti.booking.repository.UserRepository;
import com.hilti.booking.service.NotificationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;
    private final NotificationService notificationService;

    public BookingController(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            SlotRepository slotRepository,
            NotificationService notificationService
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
        this.notificationService = notificationService;
    }

    /**
     * CREATE BOOKING
     */
    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {

        User customer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Booking booking = new Booking();

        booking.setBookingNumber(
                "HB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );

        booking.setCustomer(customer);

        booking.setSoNumber(request.getSoNumber());
        booking.setSapAccountId(request.getSapAccountId());
        booking.setCompanyName(request.getCompanyName());
        booking.setAddress(request.getAddress());
        booking.setPostalCode(request.getPostalCode());

        try {

            booking.setBookingType(
                    BookingType.valueOf(request.getBookingType().toUpperCase())
            );

            booking.setTestingType(
                    TestingType.valueOf(request.getTestingType().toUpperCase())
            );

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid booking type or testing type"));
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        /**
         * SLOT LOGIC
         */
        if (request.getSlotId() != null) {

            Long slotId = request.getSlotId();
            if (slotId == null) throw new IllegalArgumentException("Slot ID cannot be null");
            Slot slot = slotRepository.findById(slotId)
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            Integer capacity =
                    slot.getCapacity() == null ? 0 : slot.getCapacity();

            Integer booked =
                    slot.getBookedCount() == null ? 0 : slot.getBookedCount();

            if (booked >= capacity) {

                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Selected slot is fully booked"));
            }

            slot.setBookedCount(booked + 1);

            slotRepository.save(slot);

            booking.setSlotType(slot.getSlotType());

            booking.setSlotDate(slot.getSlotDate());

            booking.setSlotTimeStart(slot.getSlotTime());

            booking.setSlotTimeEnd(
                    slot.getSlotTime().plusHours(2)
            );

        } else {

            if (request.getSlotType() != null) {

                try {

                    booking.setSlotType(
                            SlotType.valueOf(request.getSlotType().toUpperCase())
                    );

                } catch (IllegalArgumentException ex) {

                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Invalid slot type"));
                }
            }

            if (request.getSlotDateTimeStart() != null) {

                booking.setSlotDate(
                        request.getSlotDateTimeStart().toLocalDate()
                );

                booking.setSlotTimeStart(
                        request.getSlotDateTimeStart().toLocalTime()
                );
            }

            if (request.getSlotDateTimeEnd() != null) {

                booking.setSlotTimeEnd(
                        request.getSlotDateTimeEnd().toLocalTime()
                );
            }
        }

        /**
         * ANCHOR DETAILS
         */
        if (request.getAnchorDetail() != null) {

            AnchorDetail detail = new AnchorDetail();

            detail.setAnchorCategory(
                    request.getAnchorDetail().getAnchorCategory()
            );

            detail.setAnchorType(
                    request.getAnchorDetail().getAnchorType()
            );

            detail.setAnchorSize(
                    request.getAnchorDetail().getAnchorSize()
            );

            detail.setAnchorQuantity(
                    request.getAnchorDetail().getAnchorQuantity()
            );

            detail.setProofLoadValue(
                    request.getAnchorDetail().getProofLoadValue()
            );

            booking.setAnchorDetail(detail);
        }

        /**
         * ONSITE CONTACTS
         */
        if (request.getOnsiteContacts() != null) {

            booking.setOnsiteContacts(

                    request.getOnsiteContacts()
                            .stream()
                            .map(item -> {

                                OnsiteContact contact = new OnsiteContact();

                                contact.setName(item.getName());

                                contact.setContactNumber(
                                        item.getContactNumber()
                                );

                                contact.setEmail(item.getEmail());

                                return contact;

                            }).collect(Collectors.toList())
            );
        }

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notify(
                customer,
                "BOOKING_CONFIRMED",
                "Your booking " +
                        savedBooking.getBookingNumber() +
                        " has been confirmed.",
                "EMAIL"
        );

        return ResponseEntity.ok(savedBooking);
    }

    /**
     * GET BOOKINGS
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getBookings(
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_CUSTOMER) {

            return ResponseEntity.ok(
                    bookingRepository.findByCustomer(user)
            );
        }

        if (user.getRole() == Role.ROLE_FE) {

            return ResponseEntity.ok(
                    bookingRepository.findByFieldExecutive(user)
            );
        }

        return ResponseEntity.ok(
                bookingRepository.findAll()
        );
    }

    /**
     * GET BOOKING BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_CUSTOMER) {

            if (
                    booking.getCustomer() == null ||
                    !booking.getCustomer().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        return ResponseEntity.ok(booking);
    }

    /**
     * UPDATE BOOKING
     */
    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingRequest request
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (request.getCompanyName() != null) {
            booking.setCompanyName(request.getCompanyName());
        }

        if (request.getAddress() != null) {
            booking.setAddress(request.getAddress());
        }

        if (request.getPostalCode() != null) {
            booking.setPostalCode(request.getPostalCode());
        }

        if (request.getSoNumber() != null) {
            booking.setSoNumber(request.getSoNumber());
        }

        if (request.getSapAccountId() != null) {
            booking.setSapAccountId(request.getSapAccountId());
        }

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(booking);
    }

    /**
     * CANCEL BOOKING
     */
    @PostMapping("/{id}/cancel")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam String cancelType,
            @RequestParam(required = false) String reason
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_CUSTOMER) {

            if (
                    booking.getCustomer() == null ||
                    !booking.getCustomer().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Completed booking cannot be cancelled"));
        }

        Cancellation cancellation = new Cancellation();

        cancellation.setBooking(booking);

        cancellation.setCancelledBy(user.getEmail());

        cancellation.setCancelType(
                CancelType.valueOf(cancelType.toUpperCase())
        );

        cancellation.setReason(reason);

        booking.getCancellations().add(cancellation);

        booking.setStatus(BookingStatus.CANCELLED);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        notificationService.notify(
                booking.getCustomer(),
                "BOOKING_CANCELLED",
                "Your booking " +
                        booking.getBookingNumber() +
                        " has been cancelled.",
                "EMAIL"
        );

        return ResponseEntity.ok(
                new ApiResponse(true, "Booking cancelled successfully")
        );
    }

    /**
     * POSTPONE BOOKING
     */
    @PostMapping("/{id}/postpone")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> postponeBooking(
            @PathVariable Long id,
            @RequestParam String newDate,
            @RequestParam String newTime,
            Authentication authentication
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_CUSTOMER) {

            if (
                    booking.getCustomer() == null ||
                    !booking.getCustomer().getId().equals(user.getId())
            ) {

                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "Access denied"));
            }
        }

        booking.setSlotDate(LocalDate.parse(newDate));

        booking.setSlotTimeStart(LocalTime.parse(newTime));

        booking.setSlotTimeEnd(
                LocalTime.parse(newTime).plusHours(2)
        );

        booking.setStatus(BookingStatus.POSTPONED);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        notificationService.notify(
                booking.getCustomer(),
                "BOOKING_POSTPONED",
                "Your booking " +
                        booking.getBookingNumber() +
                        " has been postponed.",
                "EMAIL"
        );

        return ResponseEntity.ok(
                new ApiResponse(true, "Booking postponed successfully")
        );
    }

    /**
     * CUSTOMER START TESTING
     */
    @PostMapping("/{id}/testing-start")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse> startTesting(
            @PathVariable Long id,
            Authentication authentication
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (
                booking.getCustomer() == null ||
                !booking.getCustomer().getId().equals(user.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                new ApiResponse(true, "Testing started")
        );
    }

    /**
     * ASSIGN FIELD EXECUTIVE
     */
    @PostMapping("/{id}/assign")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> assignFieldExecutive(
            @PathVariable Long id,
            @RequestParam String feEmail
    ) {

        if (id == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User fe = userRepository.findByEmail(feEmail)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (fe.getRole() != Role.ROLE_FE) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Selected user is not FE"));
        }

        booking.setFieldExecutive(fe);

        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        notificationService.notify(
                fe,
                "BOOKING_ASSIGNED",
                "Booking " +
                        booking.getBookingNumber() +
                        " assigned to you.",
                "EMAIL"
        );

        return ResponseEntity.ok(
                new ApiResponse(true, "Field Executive assigned successfully")
        );
    }
}