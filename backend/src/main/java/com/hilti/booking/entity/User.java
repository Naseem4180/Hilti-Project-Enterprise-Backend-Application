package com.hilti.booking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import com.hilti.booking.entity.Role;
import com.hilti.booking.entity.CustomerType;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * USERNAME
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * EMAIL
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * PASSWORD
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * FULL NAME
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * MOBILE NUMBER
     */
    @Column(name = "mobile_number")
    private String mobileNumber;

    /**
     * ROLE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * CUSTOMER TYPE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type")
    private CustomerType customerType;

    /**
     * CITY
     */
    private String city;

    /**
     * BRANCH
     */
    private String branch;

    /**
     * ACTIVE STATUS
     */
    @Column(name = "is_active")
    private boolean isActive = true;

    /**
     * LAST LOGIN
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

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

    public User() {
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
     * USERNAME
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
     * PASSWORD HASH
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * FULL NAME
     */
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * MOBILE NUMBER
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * ROLE
     */
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * CUSTOMER TYPE
     */
    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    /**
     * CITY
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * BRANCH
     */
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * ACTIVE
     */
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * LAST LOGIN
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
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
