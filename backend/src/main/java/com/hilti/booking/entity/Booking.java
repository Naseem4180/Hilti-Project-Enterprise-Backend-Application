package com.hilti.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingNumber;

    private String soNumber;
    private String sapAccountId;
    private String companyName;
    private String address;
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    private SlotType slotType;

    @Enumerated(EnumType.STRING)
    private TestingType testingType;

    private LocalDateTime slotDateTimeStart;
    private LocalDateTime slotDateTimeEnd;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fe_id")
    private User fieldExecutive;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AnchorDetail anchorDetail;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnsiteContact> onsiteContacts = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cancellation> cancellations = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TestingReport testingReport;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getSapAccountId() {
        return sapAccountId;
    }

    public void setSapAccountId(String sapAccountId) {
        this.sapAccountId = sapAccountId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public TestingType getTestingType() {
        return testingType;
    }

    public void setTestingType(TestingType testingType) {
        this.testingType = testingType;
    }

    public LocalDateTime getSlotDateTimeStart() {
        return slotDateTimeStart;
    }

    public void setSlotDateTimeStart(LocalDateTime slotDateTimeStart) {
        this.slotDateTimeStart = slotDateTimeStart;
    }

    public LocalDateTime getSlotDateTimeEnd() {
        return slotDateTimeEnd;
    }

    public void setSlotDateTimeEnd(LocalDateTime slotDateTimeEnd) {
        this.slotDateTimeEnd = slotDateTimeEnd;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getFieldExecutive() {
        return fieldExecutive;
    }

    public void setFieldExecutive(User fieldExecutive) {
        this.fieldExecutive = fieldExecutive;
    }

    public AnchorDetail getAnchorDetail() {
        return anchorDetail;
    }

    public void setAnchorDetail(AnchorDetail anchorDetail) {
        this.anchorDetail = anchorDetail;
        if (anchorDetail != null) {
            anchorDetail.setBooking(this);
        }
    }

    public List<OnsiteContact> getOnsiteContacts() {
        return onsiteContacts;
    }

    public void setOnsiteContacts(List<OnsiteContact> onsiteContacts) {
        this.onsiteContacts = onsiteContacts;
        for (OnsiteContact contact : onsiteContacts) {
            contact.setBooking(this);
        }
    }

    public List<Cancellation> getCancellations() {
        return cancellations;
    }

    public void setCancellations(List<Cancellation> cancellations) {
        this.cancellations = cancellations;
    }

    public TestingReport getTestingReport() {
        return testingReport;
    }

    public void setTestingReport(TestingReport testingReport) {
        this.testingReport = testingReport;
        if (testingReport != null) {
            testingReport.setBooking(this);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
