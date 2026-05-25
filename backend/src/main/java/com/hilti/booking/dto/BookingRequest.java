package com.hilti.booking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequest {
    private String soNumber;
    private String sapAccountId;
    private String companyName;
    private String address;
    private String postalCode;
    private String bookingType;
    private String slotType;
    private String testingType;
    private Long slotId;
    private LocalDateTime slotDateTimeStart;
    private LocalDateTime slotDateTimeEnd;
    private AnchorDetailRequest anchorDetail;
    private List<OnsiteContactRequest> onsiteContacts;

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
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

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getTestingType() {
        return testingType;
    }

    public void setTestingType(String testingType) {
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

    public AnchorDetailRequest getAnchorDetail() {
        return anchorDetail;
    }

    public void setAnchorDetail(AnchorDetailRequest anchorDetail) {
        this.anchorDetail = anchorDetail;
    }

    public List<OnsiteContactRequest> getOnsiteContacts() {
        return onsiteContacts;
    }

    public void setOnsiteContacts(List<OnsiteContactRequest> onsiteContacts) {
        this.onsiteContacts = onsiteContacts;
    }

    public static class AnchorDetailRequest {
        private String anchorCategory;
        private String anchorType;
        private String anchorSize;
        private Integer anchorQuantity;
        private String proofLoadValue;

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

    public static class OnsiteContactRequest {
        private String name;
        private String contactNumber;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
