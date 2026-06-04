package com.bankplatform.transaction.dto;

import com.bankplatform.transaction.entity.Transaction;
import com.bankplatform.transaction.entity.TransactionStatus;
import com.bankplatform.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponse {

    private UUID id;
    private String reference;
    private TransactionType type;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private BigDecimal fees;
    private BigDecimal commission;
    private String currency;
    private TransactionStatus status;
    private String description;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    public TransactionResponse() {}

    public static TransactionResponse from(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.id = t.getId();
        r.reference = t.getReference();
        r.type = t.getType();
        r.sourceAccountId = t.getSourceAccountId();
        r.destinationAccountId = t.getDestinationAccountId();
        r.amount = t.getAmount();
        r.fees = t.getFees();
        r.commission = t.getCommission();
        r.currency = t.getCurrency();
        r.status = t.getStatus();
        r.description = t.getDescription();
        r.initiatedAt = t.getInitiatedAt();
        r.completedAt = t.getCompletedAt();
        return r;
    }

    // Getters
    public UUID getId()                  { return id; }
    public String getReference()         { return reference; }
    public TransactionType getType()     { return type; }
    public UUID getSourceAccountId()     { return sourceAccountId; }
    public UUID getDestinationAccountId(){ return destinationAccountId; }
    public BigDecimal getAmount()        { return amount; }
    public BigDecimal getFees()          { return fees; }
    public BigDecimal getCommission()    { return commission; }
    public String getCurrency()          { return currency; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription()       { return description; }
    public LocalDateTime getInitiatedAt(){ return initiatedAt; }
    public LocalDateTime getCompletedAt(){ return completedAt; }
}
