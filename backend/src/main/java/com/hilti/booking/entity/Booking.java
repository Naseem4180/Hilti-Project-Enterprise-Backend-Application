package com.hilti.booking.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_number", nullable = false, unique = true)
    private String bookingNumber;

    @Column(name = "so_number")
    private String soNumber;

    @Column(name = "sap_account_id")
    private String sapAccountId;

    @Column(name = "company_name")
    private String companyName;

    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type")
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type")
    private SlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(name = "testing_type")
    private TestingType testingType;

    @Column(name = "slot_date")
    private LocalDate slotDate;

    @Column(name = "slot_time_start")
    private LocalTime slotTimeStart;

    @Column(name = "slot_time_end")
    private LocalTime slotTimeEnd;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.DRAFT;

    /**
     * CUSTOMER
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    /**
     * FIELD EXECUTIVE
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fe_id")
    private User fieldExecutive;

    /**
     * ANCHOR DETAILS
     */
    @OneToOne(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private AnchorDetail anchorDetail;

    /**
     * ONSITE CONTACTS
     */
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OnsiteContact> onsiteContacts = new ArrayList<>();

    /**
     * CANCELLATIONS
     */
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Cancellation> cancellations = new ArrayList<>();

    /**
     * TESTING REPORT
     */
    @OneToOne(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private TestingReport testingReport;

    /**
     * LIVE TRACKING
     */
    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "last_location_updated_at")
    private LocalDateTime lastLocationUpdatedAt;

    /**
     * AUDIT
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Booking() {
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
     * BOOKING NUMBER
     */
    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    /**
     * SO NUMBER
     */
    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    /**
     * SAP ACCOUNT ID
     */
    public String getSapAccountId() {
        return sapAccountId;
    }

    public void setSapAccountId(String sapAccountId) {
        this.sapAccountId = sapAccountId;
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
     * ADDRESS
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * POSTAL CODE
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * BOOKING TYPE
     */
    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    /**
     * SLOT TYPE
     */
    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    /**
     * TESTING TYPE
     */
    public TestingType getTestingType() {
        return testingType;
    }

    public void setTestingType(TestingType testingType) {
        this.testingType = testingType;
    }

    /**
     * SLOT DATE
     */
    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    /**
     * SLOT START
     */
    public LocalTime getSlotTimeStart() {
        return slotTimeStart;
    }

    public void setSlotTimeStart(LocalTime slotTimeStart) {
        this.slotTimeStart = slotTimeStart;
    }

    /**
     * SLOT END
     */
    public LocalTime getSlotTimeEnd() {
        return slotTimeEnd;
    }

    public void setSlotTimeEnd(LocalTime slotTimeEnd) {
        this.slotTimeEnd = slotTimeEnd;
    }

    /**
     * STATUS
     */
    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * CUSTOMER
     */
    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
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
     * ANCHOR DETAIL
     */
    public AnchorDetail getAnchorDetail() {
        return anchorDetail;
    }

    public void setAnchorDetail(AnchorDetail anchorDetail) {

        this.anchorDetail = anchorDetail;

        if (anchorDetail != null) {
            anchorDetail.setBooking(this);
        }
    }

    /**
     * ONSITE CONTACTS
     */
    public List<OnsiteContact> getOnsiteContacts() {
        return onsiteContacts;
    }

    public void setOnsiteContacts(List<OnsiteContact> onsiteContacts) {

        this.onsiteContacts = onsiteContacts;

        if (onsiteContacts != null) {

            for (OnsiteContact contact : onsiteContacts) {
                contact.setBooking(this);
            }
        }
    }

    /**
     * CANCELLATIONS
     */
    public List<Cancellation> getCancellations() {
        return cancellations;
    }

    public void setCancellations(List<Cancellation> cancellations) {
        this.cancellations = cancellations;
    }

    /**
     * TESTING REPORT
     */
    public TestingReport getTestingReport() {
        return testingReport;
    }

    public void setTestingReport(TestingReport testingReport) {

        this.testingReport = testingReport;

        if (testingReport != null) {
            testingReport.setBooking(this);
        }
    }

    /**
     * CURRENT LATITUDE
     */
    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    /**
     * CURRENT LONGITUDE
     */
    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    /**
     * LAST LOCATION UPDATED
     */
    public LocalDateTime getLastLocationUpdatedAt() {
        return lastLocationUpdatedAt;
    }

    public void setLastLocationUpdatedAt(
            LocalDateTime lastLocationUpdatedAt
    ) {
        this.lastLocationUpdatedAt = lastLocationUpdatedAt;
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