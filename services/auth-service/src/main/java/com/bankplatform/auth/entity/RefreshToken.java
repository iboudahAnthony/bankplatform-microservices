package com.bankplatform.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public RefreshToken() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RefreshToken rt = new RefreshToken();
        public Builder user(User v)          { rt.user = v; return this; }
        public Builder token(String v)       { rt.token = v; return this; }
        public Builder expiresAt(LocalDateTime v) { rt.expiresAt = v; return this; }
        public Builder createdAt(LocalDateTime v) { rt.createdAt = v; return this; }
        public RefreshToken build() { return rt; }
    }

    public boolean isValid() {
        return !isRevoked && expiresAt.isAfter(LocalDateTime.now());
    }

    // Getters & Setters
    public UUID getId()                        { return id; }
    public User getUser()                      { return user; }
    public String getToken()                   { return token; }
    public LocalDateTime getExpiresAt()        { return expiresAt; }
    public Boolean getIsRevoked()              { return isRevoked; }
    public void setIsRevoked(Boolean v)        { this.isRevoked = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
}
