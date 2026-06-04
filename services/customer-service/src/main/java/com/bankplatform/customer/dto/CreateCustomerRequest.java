package com.bankplatform.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CreateCustomerRequest {

    private UUID userId;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Email
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String phoneNumber;
    private String nationality;
    private String address;
    private String city;
    private String country;
    private UUID operatorId;

    // Getters & Setters
    public UUID getUserId()             { return userId; }
    public void setUserId(UUID v)       { this.userId = v; }
    public String getFirstName()        { return firstName; }
    public void setFirstName(String v)  { this.firstName = v; }
    public String getLastName()         { return lastName; }
    public void setLastName(String v)   { this.lastName = v; }
    public String getEmail()            { return email; }
    public void setEmail(String v)      { this.email = v; }
    public String getPhoneNumber()      { return phoneNumber; }
    public void setPhoneNumber(String v){ this.phoneNumber = v; }
    public String getNationality()      { return nationality; }
    public void setNationality(String v){ this.nationality = v; }
    public String getAddress()          { return address; }
    public void setAddress(String v)    { this.address = v; }
    public String getCity()             { return city; }
    public void setCity(String v)       { this.city = v; }
    public String getCountry()          { return country; }
    public void setCountry(String v)    { this.country = v; }
    public UUID getOperatorId()         { return operatorId; }
    public void setOperatorId(UUID v)   { this.operatorId = v; }
}
