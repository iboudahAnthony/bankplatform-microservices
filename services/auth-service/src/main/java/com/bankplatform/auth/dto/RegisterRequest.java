package com.bankplatform.auth.dto;

import com.bankplatform.auth.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    private String phoneNumber;
    private UserRole role = UserRole.CLIENT;

    // Getters & Setters
    public String getFirstName()              { return firstName; }
    public void setFirstName(String v)        { this.firstName = v; }
    public String getLastName()               { return lastName; }
    public void setLastName(String v)         { this.lastName = v; }
    public String getEmail()                  { return email; }
    public void setEmail(String v)            { this.email = v; }
    public String getPassword()               { return password; }
    public void setPassword(String v)         { this.password = v; }
    public String getPhoneNumber()            { return phoneNumber; }
    public void setPhoneNumber(String v)      { this.phoneNumber = v; }
    public UserRole getRole()                 { return role; }
    public void setRole(UserRole v)           { this.role = v; }
}
