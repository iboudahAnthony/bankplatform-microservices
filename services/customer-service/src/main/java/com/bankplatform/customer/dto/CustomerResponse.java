package com.bankplatform.customer.dto;

import com.bankplatform.customer.entity.Customer;
import com.bankplatform.customer.entity.CustomerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerResponse {

    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationality;
    private String address;
    private String city;
    private String country;
    private CustomerStatus status;
    private Integer creditScore;
    private UUID operatorId;
    private LocalDateTime createdAt;

    public CustomerResponse() {}

    // Conversion depuis entité
    public static CustomerResponse from(Customer c) {
        CustomerResponse r = new CustomerResponse();
        r.id = c.getId();
        r.userId = c.getUserId();
        r.firstName = c.getFirstName();
        r.lastName = c.getLastName();
        r.email = c.getEmail();
        r.phoneNumber = c.getPhoneNumber();
        r.nationality = c.getNationality();
        r.address = c.getAddress();
        r.city = c.getCity();
        r.country = c.getCountry();
        r.status = c.getStatus();
        r.creditScore = c.getCreditScore();
        r.operatorId = c.getOperatorId();
        r.createdAt = c.getCreatedAt();
        return r;
    }

    // Getters
    public UUID getId()              { return id; }
    public UUID getUserId()          { return userId; }
    public String getFirstName()     { return firstName; }
    public String getLastName()      { return lastName; }
    public String getEmail()         { return email; }
    public String getPhoneNumber()   { return phoneNumber; }
    public String getNationality()   { return nationality; }
    public String getAddress()       { return address; }
    public String getCity()          { return city; }
    public String getCountry()       { return country; }
    public CustomerStatus getStatus(){ return status; }
    public Integer getCreditScore()  { return creditScore; }
    public UUID getOperatorId()      { return operatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
