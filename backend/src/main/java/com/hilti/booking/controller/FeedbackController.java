package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.dto.FeedbackRequest;
import com.hilti.booking.entity.Booking;
import com.hilti.booking.entity.BookingStatus;
import com.hilti.booking.entity.Feedback;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.BookingRepository;
import com.hilti.booking.repository.FeedbackRepository;
import com.hilti.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public FeedbackController(
            FeedbackRepository feedbackRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * SUBMIT FEEDBACK
     */
    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication
    ) {

        User customer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Long bookingId = request.getBookingId();
        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * VALIDATE BOOKING OWNER
         */
        if (
                booking.getCustomer() == null ||
                !booking.getCustomer().getId().equals(customer.getId())
        ) {

            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Access denied"));
        }

        /**
         * FEEDBACK ONLY AFTER COMPLETION
         */
        if (booking.getStatus() != BookingStatus.COMPLETED) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Feedback can only be submitted after booking completion"
                    ));
        }

        /**
         * PREVENT DUPLICATE FEEDBACK
         */
        boolean feedbackExists =
                feedbackRepository.existsByBooking(booking);

        if (feedbackExists) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Feedback already submitted for this booking"
                    ));
        }

        /**
         * VALIDATE RATING
         */
        if (
                request.getRating() < 1 ||
                request.getRating() > 5
        ) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Rating must be between 1 and 5"
                    ));
        }

        Feedback feedback = new Feedback();

        feedback.setBooking(booking);

        feedback.setCustomer(customer);

        feedback.setRating(request.getRating());

        feedback.setComments(request.getComments());

        feedback.setSubmittedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return ResponseEntity.ok(savedFeedback);
    }

    /**
     * GET FEEDBACK LIST
     */
    @GetMapping
    public ResponseEntity<?> getFeedback(
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        /**
         * CUSTOMER CAN ONLY VIEW OWN FEEDBACK
         */
        if (user.getRole() == Role.ROLE_CUSTOMER) {

            List<Feedback> feedbackList =
                    feedbackRepository.findByCustomer(user);

            return ResponseEntity.ok(feedbackList);
        }

        /**
         * ADMIN / MANAGER CAN VIEW ALL
         */
        return ResponseEntity.ok(
                feedbackRepository.findAll()
        );
    }

    /**
     * GET FEEDBACK BY BOOKING
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getFeedbackByBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * CUSTOMER SECURITY CHECK
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

        List<Feedback> feedbackList =
                feedbackRepository.findByBooking(booking);

        if (feedbackList == null || feedbackList.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(
                            false,
                            "Feedback not found"
                    ));
        }

        return ResponseEntity.ok(feedbackList.get(0));
    }

    /**
     * DELETE FEEDBACK
     */
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> deleteFeedback(
            @PathVariable Long id
    ) {

        if (id == null) throw new IllegalArgumentException("Feedback ID cannot be null");
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedbackRepository.delete(feedback);

        return ResponseEntity.ok(
                new ApiResponse(true, "Feedback deleted successfully")
        );
    }
}