package com.hilti.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_registrations")
public class CustomerRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * EMAIL
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * FIRST NAME
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * LAST NAME
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * COMPANY NAME
     */
    @Column(name = "company_name", nullable = false)
    private String companyName;

    /**
     * CUSTOMER ACCOUNT NUMBER
     */
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    /**
     * CONTACT NUMBER
     */
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    /**
     * REGISTRATION STATUS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    /**
     * REJECTION REASON
     */
    @Column(name = "rejection_reason")
    private String rejectionReason;

    /**
     * APPROVED BY ADMIN
     */
    @Column(name = "approved_by")
    private String approvedBy;

    /**
     * CREATED AT
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * UPDATED AT
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public CustomerRegistration() {
    }

    /**
     * ID
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * EMAIL
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * FIRST NAME
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * LAST NAME
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * COMPANY NAME
     */
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * ACCOUNT NUMBER
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * CONTACT NUMBER
     */
    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * STATUS
     */
    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    /**
     * REJECTION REASON
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * APPROVED BY
     */
    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    /**
     * CREATED AT
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * UPDATED AT
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
