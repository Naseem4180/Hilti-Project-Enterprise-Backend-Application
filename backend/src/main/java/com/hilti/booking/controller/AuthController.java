package com.hilti.booking.controller;

import com.hilti.booking.dto.ApiResponse;
import com.hilti.booking.dto.CustomerRegistrationRequest;
import com.hilti.booking.dto.JwtResponse;
import com.hilti.booking.dto.LoginRequest;
import com.hilti.booking.dto.RegisterRequest;
import com.hilti.booking.entity.CustomerRegistration;
import com.hilti.booking.entity.CustomerType;
import com.hilti.booking.entity.PasswordResetToken;
import com.hilti.booking.entity.RegistrationStatus;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.User;
import com.hilti.booking.repository.CustomerRegistrationRepository;
import com.hilti.booking.repository.PasswordResetTokenRepository;
import com.hilti.booking.repository.UserRepository;
import com.hilti.booking.security.JwtUtils;
import com.hilti.booking.service.EmailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final CustomerRegistrationRepository customerRegistrationRepository;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            CustomerRegistrationRepository customerRegistrationRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.customerRegistrationRepository = customerRegistrationRepository;
    }

    /**
     * Register customer
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email already registered"));
        }

        if (
                request.getUsername() != null &&
                !request.getUsername().isBlank() &&
                userRepository.existsByUsername(request.getUsername())
        ) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username already exists"));
        }

        User user = new User();

        user.setFullName(request.getFullName());

        user.setEmail(request.getEmail());

        user.setUsername(
                request.getUsername() != null &&
                !request.getUsername().isBlank()
                        ? request.getUsername()
                        : request.getEmail().split("@")[0]
        );

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.ROLE_CUSTOMER);

        user.setActive(true);

        CustomerType customerType = CustomerType.NORMAL;

        if (
                request.getCustomerType() != null &&
                !request.getCustomerType().isBlank()
        ) {
            try {
                customerType = CustomerType.valueOf(
                        request.getCustomerType().toUpperCase()
                );
            } catch (IllegalArgumentException ignored) {
            }
        }

        user.setCustomerType(customerType);

        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse(true, "Registration successful")
        );
    }

    /**
     * CUSTOMER REGISTRATION WITH APPROVAL
     */
    @PostMapping("/register-customer")
    @Transactional
    public ResponseEntity<ApiResponse> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request
    ) {

        // Check if email already exists in user table or registration table
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email already registered"));
        }

        if (customerRegistrationRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email already submitted for registration"));
        }

        // Create customer registration record
        CustomerRegistration registration = new CustomerRegistration();
        registration.setEmail(request.getEmail());
        registration.setFirstName(request.getFirstName());
        registration.setLastName(request.getLastName());
        registration.setCompanyName(request.getCompanyName());
        registration.setAccountNumber(request.getAccountNumber());
        registration.setContactNumber(request.getContactNumber());
        registration.setStatus(RegistrationStatus.PENDING);

        customerRegistrationRepository.save(registration);

        return ResponseEntity.ok(
                new ApiResponse(true, "Registration submitted. Please wait for admin approval.")
        );
    }

    /**
     * Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException ex) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid email or password"));
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Account is disabled"));
        }

        String token = jwtUtils.generateToken(user.getEmail());

        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok(
                new JwtResponse(
                        token,
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name()
                )
        );
    }

    /**
     * Forgot password
     */
    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestParam String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email not found"));
        }

        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken token = new PasswordResetToken();

        token.setUser(user);

        token.setToken(UUID.randomUUID().toString());

        token.setExpiresAt(
                LocalDateTime.now().plusMinutes(30)
        );

        token.setUsed(false);

        passwordResetTokenRepository.save(token);

        emailService.sendEmail(
                user.getEmail(),
                "Hilti Password Reset",
                "Your reset token is: " + token.getToken()
        );

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Password reset token sent to email"
                )
        );
    }

    /**
     * Reset password
     */
    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElse(null);

        if (
                resetToken == null ||
                resetToken.isUsed() ||
                resetToken.getExpiresAt().isBefore(LocalDateTime.now())
        ) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid or expired token"));
        }

        User user = resetToken.getUser();

        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);

        return ResponseEntity.ok(
                new ApiResponse(true, "Password reset successful")
        );
    }
}