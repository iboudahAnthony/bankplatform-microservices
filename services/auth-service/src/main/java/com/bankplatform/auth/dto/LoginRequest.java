package com.bankplatform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    // Getters & Setters
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getPassword()        { return password; }
    public void setPassword(String v)  { this.password = v; }
}
