package com.hilti.booking.repository;

import com.hilti.booking.entity.CustomerRegistration;
import com.hilti.booking.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRegistrationRepository extends JpaRepository<CustomerRegistration, Long> {

    /**
     * Find by email
     */
    Optional<CustomerRegistration> findByEmail(String email);

    /**
     * Find by status
     */
    List<CustomerRegistration> findByStatus(RegistrationStatus status);
}
