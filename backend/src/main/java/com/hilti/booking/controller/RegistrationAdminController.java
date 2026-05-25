package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.entity.CustomerRegistration;
import com.hilti.booking.entity.CustomerType;
import com.hilti.booking.entity.RegistrationStatus;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.CustomerRegistrationRepository;
import com.hilti.booking.repository.UserRepository;
import com.hilti.booking.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/registrations")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
public class RegistrationAdminController {

    private final CustomerRegistrationRepository customerRegistrationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public RegistrationAdminController(
            CustomerRegistrationRepository customerRegistrationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            NotificationService notificationService
    ) {
        this.customerRegistrationRepository = customerRegistrationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    /**
     * GET PENDING REGISTRATIONS
     */
    @GetMapping("/pending")
    public ResponseEntity<List<CustomerRegistration>> getPendingRegistrations() {
        List<CustomerRegistration> pending = customerRegistrationRepository
                .findByStatus(RegistrationStatus.PENDING);
        return ResponseEntity.ok(pending);
    }

    /**
     * GET APPROVED REGISTRATIONS
     */
    @GetMapping("/approved")
    public ResponseEntity<List<CustomerRegistration>> getApprovedRegistrations() {
        List<CustomerRegistration> approved = customerRegistrationRepository
                .findByStatus(RegistrationStatus.APPROVED);
        return ResponseEntity.ok(approved);
    }

    /**
     * GET REJECTED REGISTRATIONS
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<CustomerRegistration>> getRejectedRegistrations() {
        List<CustomerRegistration> rejected = customerRegistrationRepository
                .findByStatus(RegistrationStatus.REJECTED);
        return ResponseEntity.ok(rejected);
    }

    /**
     * GET REGISTRATION BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegistrationById(@PathVariable Long id) {
        CustomerRegistration registration = customerRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return ResponseEntity.ok(registration);
    }

    /**
     * APPROVE REGISTRATION AND GENERATE CREDENTIALS
     */
    @PostMapping("/{id}/approve")
    @Transactional
    public ResponseEntity<?> approveRegistration(
            @PathVariable Long id
    ) {

        CustomerRegistration registration = customerRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Only pending registrations can be approved"));
        }

        // Generate username and password
        String username = generateUsername(registration.getFirstName(), registration.getLastName());
        String tempPassword = generateTempPassword();

        // Create user account
        User user = new User();
        user.setEmail(registration.getEmail());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setFullName(registration.getFirstName() + " " + registration.getLastName());
        user.setRole(Role.ROLE_CUSTOMER);
        user.setCustomerType(CustomerType.NORMAL);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Update registration status
        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setUpdatedAt(LocalDateTime.now());
        customerRegistrationRepository.save(registration);

        // Send credentials via email
        notificationService.notify(
                user,
                "REGISTRATION_APPROVED",
                "Your registration has been approved! \n\n" +
                        "Username: " + username + "\n" +
                        "Temporary Password: " + tempPassword + "\n\n" +
                        "Please login and change your password.",
                "EMAIL"
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Registration approved",
                        "username", username,
                        "tempPassword", tempPassword
                )
        );
    }

    /**
     * REJECT REGISTRATION
     */
    @PostMapping("/{id}/reject")
    @Transactional
    public ResponseEntity<?> rejectRegistration(
            @PathVariable Long id,
            @RequestParam String reason
    ) {

        CustomerRegistration registration = customerRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Only pending registrations can be rejected"));
        }

        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registration.setUpdatedAt(LocalDateTime.now());
        customerRegistrationRepository.save(registration);

        // Note: Email would be sent separately via a notification service
        // For now, rejection is recorded in the database
        // In production, implement actual email sending to registration.getEmail()

        return ResponseEntity.ok(
                new ApiResponse(true, "Registration rejected successfully")
        );
    }

    /**
     * GENERATE USERNAME
     */
    private String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName.substring(0, 1) + lastName)
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        // Ensure uniqueness
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    /**
     * GENERATE TEMPORARY PASSWORD
     */
    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
