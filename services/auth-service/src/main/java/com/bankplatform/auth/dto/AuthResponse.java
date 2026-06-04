package com.bankplatform.auth.dto;

import java.util.UUID;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UUID userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;

    public AuthResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder accessToken(String v)    { r.accessToken = v; return this; }
        public Builder refreshToken(String v)   { r.refreshToken = v; return this; }
        public Builder tokenType(String v)      { r.tokenType = v; return this; }
        public Builder expiresIn(Long v)        { r.expiresIn = v; return this; }
        public Builder userId(UUID v)           { r.userId = v; return this; }
        public Builder email(String v)          { r.email = v; return this; }
        public Builder role(String v)           { r.role = v; return this; }
        public Builder firstName(String v)      { r.firstName = v; return this; }
        public Builder lastName(String v)       { r.lastName = v; return this; }
        public AuthResponse build()             { return r; }
    }

    // Getters
    public String getAccessToken()   { return accessToken; }
    public String getRefreshToken()  { return refreshToken; }
    public String getTokenType()     { return tokenType; }
    public Long getExpiresIn()       { return expiresIn; }
    public UUID getUserId()          { return userId; }
    public String getEmail()         { return email; }
    public String getRole()          { return role; }
    public String getFirstName()     { return firstName; }
    public String getLastName()      { return lastName; }
}
