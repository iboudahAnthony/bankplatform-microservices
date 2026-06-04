package com.bankplatform.transaction.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "source_account_id")
    private UUID sourceAccountId;

    @Column(name = "destination_account_id")
    private UUID destinationAccountId;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @Column(precision = 20, scale = 2)
    private BigDecimal fees = BigDecimal.ZERO;

    @Column(precision = 20, scale = 2)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency = "XOF";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String description;

    @Column(name = "operator_source_id")
    private UUID operatorSourceId;

    @Column(name = "operator_destination_id")
    private UUID operatorDestinationId;

    @Column(name = "initiated_by")
    private UUID initiatedBy;

    @CreationTimestamp
    @Column(name = "initiated_at", updatable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Transaction() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Transaction t = new Transaction();
        public Builder reference(String v)              { t.reference = v; return this; }
        public Builder type(TransactionType v)          { t.type = v; return this; }
        public Builder sourceAccountId(UUID v)          { t.sourceAccountId = v; return this; }
        public Builder destinationAccountId(UUID v)     { t.destinationAccountId = v; return this; }
        public Builder amount(BigDecimal v)             { t.amount = v; return this; }
        public Builder fees(BigDecimal v)               { t.fees = v; return this; }
        public Builder commission(BigDecimal v)         { t.commission = v; return this; }
        public Builder currency(String v)               { t.currency = v; return this; }
        public Builder status(TransactionStatus v)      { t.status = v; return this; }
        public Builder description(String v)            { t.description = v; return this; }
        public Builder operatorSourceId(UUID v)         { t.operatorSourceId = v; return this; }
        public Builder operatorDestinationId(UUID v)    { t.operatorDestinationId = v; return this; }
        public Builder initiatedBy(UUID v)              { t.initiatedBy = v; return this; }
        public Transaction build() { return t; }
    }

    // Getters & Setters
    public UUID getId()                             { return id; }
    public String getReference()                    { return reference; }
    public TransactionType getType()                { return type; }
    public UUID getSourceAccountId()                { return sourceAccountId; }
    public UUID getDestinationAccountId()           { return destinationAccountId; }
    public BigDecimal getAmount()                   { return amount; }
    public BigDecimal getFees()                     { return fees; }
    public BigDecimal getCommission()               { return commission; }
    public String getCurrency()                     { return currency; }
    public TransactionStatus getStatus()            { return status; }
    public void setStatus(TransactionStatus v)      { this.status = v; }
    public String getDescription()                  { return description; }
    public UUID getOperatorSourceId()               { return operatorSourceId; }
    public UUID getOperatorDestinationId()          { return operatorDestinationId; }
    public UUID getInitiatedBy()                    { return initiatedBy; }
    public LocalDateTime getInitiatedAt()           { return initiatedAt; }
    public LocalDateTime getCompletedAt()           { return completedAt; }
    public void setCompletedAt(LocalDateTime v)     { this.completedAt = v; }
}
