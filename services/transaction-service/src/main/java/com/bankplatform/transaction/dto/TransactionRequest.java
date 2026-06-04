package com.bankplatform.transaction.dto;

import com.bankplatform.transaction.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequest {

    @NotNull(message = "Le type est obligatoire")
    private TransactionType type;

    private UUID sourceAccountId;
    private UUID destinationAccountId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être positif")
    private BigDecimal amount;

    private String currency = "XOF";
    private String description;
    private UUID initiatedBy;
    private UUID operatorSourceId;
    private UUID operatorDestinationId;

    // Getters & Setters
    public TransactionType getType()             { return type; }
    public void setType(TransactionType v)       { this.type = v; }
    public UUID getSourceAccountId()             { return sourceAccountId; }
    public void setSourceAccountId(UUID v)       { this.sourceAccountId = v; }
    public UUID getDestinationAccountId()        { return destinationAccountId; }
    public void setDestinationAccountId(UUID v)  { this.destinationAccountId = v; }
    public BigDecimal getAmount()                { return amount; }
    public void setAmount(BigDecimal v)          { this.amount = v; }
    public String getCurrency()                  { return currency; }
    public void setCurrency(String v)            { this.currency = v; }
    public String getDescription()               { return description; }
    public void setDescription(String v)         { this.description = v; }
    public UUID getInitiatedBy()                 { return initiatedBy; }
    public void setInitiatedBy(UUID v)           { this.initiatedBy = v; }
    public UUID getOperatorSourceId()            { return operatorSourceId; }
    public void setOperatorSourceId(UUID v)      { this.operatorSourceId = v; }
    public UUID getOperatorDestinationId()       { return operatorDestinationId; }
    public void setOperatorDestinationId(UUID v) { this.operatorDestinationId = v; }
}
