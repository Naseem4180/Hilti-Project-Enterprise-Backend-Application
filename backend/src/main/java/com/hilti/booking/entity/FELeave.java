package com.hilti.booking.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fe_leaves")
public class FELeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FIELD EXECUTIVE
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fe_id")
    private User fieldExecutive;

    /**
     * LEAVE DATE
     */
    @Column(name = "leave_date", nullable = false)
    private LocalDate leaveDate;

    /**
     * APPROVAL STATUS
     */
    @Column(name = "is_approved")
    private boolean approved = false;

    /**
     * CREATED AT
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public FELeave() {
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
     * FIELD EXECUTIVE
     */
    public User getFieldExecutive() {
        return fieldExecutive;
    }

    public void setFieldExecutive(User fieldExecutive) {
        this.fieldExecutive = fieldExecutive;
    }

    /**
     * LEAVE DATE
     */
    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    /**
     * APPROVED
     */
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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
}