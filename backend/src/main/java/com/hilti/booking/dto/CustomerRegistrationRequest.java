package com.hilti.booking.dto;

import jakarta.validation.constraints.*;

public class CustomerRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First Name is required")
    @Size(min = 2, max = 50, message = "First Name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 2, max = 50, message = "Last Name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Company Name is required")
    @Size(min = 2, max = 100, message = "Company Name must be between 2 and 100 characters")
    private String companyName;

    @NotBlank(message = "Customer Account Number is required")
    @Size(min = 3, max = 50, message = "Account Number must be between 3 and 50 characters")
    private String accountNumber;

    @NotBlank(message = "Contact Number is required")
    @Pattern(regexp = "^\\+65\\d{8}$", message = "Contact number must be in format +65XXXXXXXX")
    private String contactNumber;

    public CustomerRegistrationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
