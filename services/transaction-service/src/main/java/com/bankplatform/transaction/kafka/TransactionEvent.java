package com.bankplatform.transaction.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionEvent {

    private String eventType;
    private UUID transactionId;
    private String reference;
    private String type;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private UUID initiatedBy;
    private LocalDateTime occurredAt;

    public TransactionEvent() {}

    public TransactionEvent(String eventType, UUID transactionId, String reference,
                            String type, UUID sourceAccountId, UUID destinationAccountId,
                            BigDecimal amount, String currency, String status, UUID initiatedBy) {
        this.eventType = eventType;
        this.transactionId = transactionId;
        this.reference = reference;
        this.type = type;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.initiatedBy = initiatedBy;
        this.occurredAt = LocalDateTime.now();
    }

    // Getters & Setters
    public String getEventType()                    { return eventType; }
    public void setEventType(String v)              { this.eventType = v; }
    public UUID getTransactionId()                  { return transactionId; }
    public void setTransactionId(UUID v)            { this.transactionId = v; }
    public String getReference()                    { return reference; }
    public void setReference(String v)              { this.reference = v; }
    public String getType()                         { return type; }
    public void setType(String v)                   { this.type = v; }
    public UUID getSourceAccountId()                { return sourceAccountId; }
    public void setSourceAccountId(UUID v)          { this.sourceAccountId = v; }
    public UUID getDestinationAccountId()           { return destinationAccountId; }
    public void setDestinationAccountId(UUID v)     { this.destinationAccountId = v; }
    public BigDecimal getAmount()                   { return amount; }
    public void setAmount(BigDecimal v)             { this.amount = v; }
    public String getCurrency()                     { return currency; }
    public void setCurrency(String v)               { this.currency = v; }
    public String getStatus()                       { return status; }
    public void setStatus(String v)                 { this.status = v; }
    public UUID getInitiatedBy()                    { return initiatedBy; }
    public void setInitiatedBy(UUID v)              { this.initiatedBy = v; }
    public LocalDateTime getOccurredAt()            { return occurredAt; }
    public void setOccurredAt(LocalDateTime v)      { this.occurredAt = v; }
}
