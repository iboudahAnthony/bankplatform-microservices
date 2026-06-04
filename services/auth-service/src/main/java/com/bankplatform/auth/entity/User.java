package com.bankplatform.auth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "operator_id")
    private UUID operatorId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeurs
    public User() {}

    // Builder pattern manuel
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder firstName(String v)    { user.firstName = v; return this; }
        public Builder lastName(String v)     { user.lastName = v; return this; }
        public Builder email(String v)        { user.email = v; return this; }
        public Builder password(String v)     { user.password = v; return this; }
        public Builder phoneNumber(String v)  { user.phoneNumber = v; return this; }
        public Builder role(UserRole v)       { user.role = v; return this; }
        public Builder isActive(Boolean v)    { user.isActive = v; return this; }
        public User build() { return user; }
    }

    // Getters & Setters
    public UUID getId()                        { return id; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPassword()                { return password; }
    public void setPassword(String password)   { this.password = password; }
    public String getFirstName()               { return firstName; }
    public void setFirstName(String v)         { this.firstName = v; }
    public String getLastName()                { return lastName; }
    public void setLastName(String v)          { this.lastName = v; }
    public String getPhoneNumber()             { return phoneNumber; }
    public void setPhoneNumber(String v)       { this.phoneNumber = v; }
    public UserRole getRole()                  { return role; }
    public void setRole(UserRole role)         { this.role = role; }
    public Boolean getIsActive()               { return isActive; }
    public void setIsActive(Boolean isActive)  { this.isActive = isActive; }
    public Integer getFailedAttempts()         { return failedAttempts; }
    public void setFailedAttempts(Integer v)   { this.failedAttempts = v; }
    public LocalDateTime getLastLoginAt()      { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime v){ this.lastLoginAt = v; }
    public UUID getCustomerId()                { return customerId; }
    public UUID getOperatorId()                { return operatorId; }
}
