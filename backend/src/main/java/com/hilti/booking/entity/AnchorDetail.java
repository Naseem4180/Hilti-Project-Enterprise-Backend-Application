package com.hilti.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "anchor_details")
public class AnchorDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "anchor_category")
    private String anchorCategory;

    @Column(name = "anchor_type")
    private String anchorType;

    @Column(name = "anchor_size")
    private String anchorSize;

    @Column(name = "anchor_quantity")
    private Integer anchorQuantity;

    @Column(name = "proof_load_value")
    private String proofLoadValue;

    public AnchorDetail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getAnchorCategory() {
        return anchorCategory;
    }

    public void setAnchorCategory(String anchorCategory) {
        this.anchorCategory = anchorCategory;
    }

    public String getAnchorType() {
        return anchorType;
    }

    public void setAnchorType(String anchorType) {
        this.anchorType = anchorType;
    }

    public String getAnchorSize() {
        return anchorSize;
    }

    public void setAnchorSize(String anchorSize) {
        this.anchorSize = anchorSize;
    }

    public Integer getAnchorQuantity() {
        return anchorQuantity;
    }

    public void setAnchorQuantity(Integer anchorQuantity) {
        this.anchorQuantity = anchorQuantity;
    }

    public String getProofLoadValue() {
        return proofLoadValue;
    }

    public void setProofLoadValue(String proofLoadValue) {
        this.proofLoadValue = proofLoadValue;
    }
}
