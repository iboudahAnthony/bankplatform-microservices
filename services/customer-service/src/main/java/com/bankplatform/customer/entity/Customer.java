package com.bankplatform.customer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String nationality;
    private String address;
    private String city;
    private String country;

    @Column(name = "operator_id")
    private UUID operatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Column(name = "credit_score")
    private Integer creditScore = 500;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Customer() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Customer c = new Customer();
        public Builder userId(UUID v)       { c.userId = v; return this; }
        public Builder firstName(String v)  { c.firstName = v; return this; }
        public Builder lastName(String v)   { c.lastName = v; return this; }
        public Builder email(String v)      { c.email = v; return this; }
        public Builder phoneNumber(String v){ c.phoneNumber = v; return this; }
        public Builder nationality(String v){ c.nationality = v; return this; }
        public Builder address(String v)    { c.address = v; return this; }
        public Builder city(String v)       { c.city = v; return this; }
        public Builder country(String v)    { c.country = v; return this; }
        public Builder status(CustomerStatus v) { c.status = v; return this; }
        public Builder operatorId(UUID v)   { c.operatorId = v; return this; }
        public Customer build() { return c; }
    }

    // Getters & Setters
    public UUID getId()                         { return id; }
    public UUID getUserId()                     { return userId; }
    public void setUserId(UUID v)               { this.userId = v; }
    public String getFirstName()                { return firstName; }
    public void setFirstName(String v)          { this.firstName = v; }
    public String getLastName()                 { return lastName; }
    public void setLastName(String v)           { this.lastName = v; }
    public String getEmail()                    { return email; }
    public void setEmail(String v)              { this.email = v; }
    public String getPhoneNumber()              { return phoneNumber; }
    public void setPhoneNumber(String v)        { this.phoneNumber = v; }
    public LocalDate getDateOfBirth()           { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)     { this.dateOfBirth = v; }
    public String getNationality()              { return nationality; }
    public void setNationality(String v)        { this.nationality = v; }
    public String getAddress()                  { return address; }
    public void setAddress(String v)            { this.address = v; }
    public String getCity()                     { return city; }
    public void setCity(String v)               { this.city = v; }
    public String getCountry()                  { return country; }
    public void setCountry(String v)            { this.country = v; }
    public UUID getOperatorId()                 { return operatorId; }
    public void setOperatorId(UUID v)           { this.operatorId = v; }
    public CustomerStatus getStatus()           { return status; }
    public void setStatus(CustomerStatus v)     { this.status = v; }
    public Integer getCreditScore()             { return creditScore; }
    public void setCreditScore(Integer v)       { this.creditScore = v; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
}
