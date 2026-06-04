package com.bankplatform.customer.controller;

import com.bankplatform.customer.dto.CreateCustomerRequest;
import com.bankplatform.customer.dto.CustomerResponse;
import com.bankplatform.customer.entity.CustomerStatus;
import com.bankplatform.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Créer un client
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    // Obtenir tous les clients
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Obtenir un client par ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // Obtenir un client par userId (appelé par auth-service)
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(customerService.getCustomerByUserId(userId));
    }

    // Obtenir un client par email
    @GetMapping("/by-email/{email}")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    // Obtenir les clients par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByStatus(
            @PathVariable CustomerStatus status) {
        return ResponseEntity.ok(customerService.getCustomersByStatus(status));
    }

    // Obtenir les clients d'un opérateur
    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByOperator(
            @PathVariable UUID operatorId) {
        return ResponseEntity.ok(customerService.getCustomersByOperator(operatorId));
    }

    // Mettre à jour un client
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    // Changer le statut d'un client
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        CustomerStatus status = CustomerStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(customerService.updateCustomerStatus(id, status));
    }

    // Vérifier un client (KYC validé)
    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verifyCustomer(@PathVariable UUID id) {
        customerService.verifyCustomer(id);
        return ResponseEntity.ok().build();
    }

    // Suspendre un client
    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendCustomer(@PathVariable UUID id) {
        customerService.suspendCustomer(id);
        return ResponseEntity.ok().build();
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "customer-service"));
    }
}
