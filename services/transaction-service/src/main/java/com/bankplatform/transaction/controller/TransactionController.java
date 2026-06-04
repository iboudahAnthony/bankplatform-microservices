package com.bankplatform.transaction.controller;

import com.bankplatform.transaction.dto.TransactionRequest;
import com.bankplatform.transaction.dto.TransactionResponse;
import com.bankplatform.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Effectuer une transaction (dépôt, retrait, transfert)
    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.processTransaction(request));
    }

    // Lister toutes les transactions
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // Obtenir une transaction par ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    // Obtenir une transaction par référence
    @GetMapping("/reference/{reference}")
    public ResponseEntity<TransactionResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(transactionService.getTransactionByReference(reference));
    }

    // Historique d'un compte
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(
            @PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "transaction-service"));
    }
}
