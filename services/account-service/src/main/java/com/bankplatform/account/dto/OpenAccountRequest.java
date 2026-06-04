package com.bankplatform.account.dto;

import com.bankplatform.account.entity.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class OpenAccountRequest {

    @NotNull(message = "L'identifiant client est obligatoire")
    private UUID customerId;

    private UUID operatorId;

    @NotNull(message = "Le type de compte est obligatoire")
    private AccountType type;

    private String currency = "XOF";
    private BigDecimal initialDeposit = BigDecimal.ZERO;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;

    // Getters & Setters
    public UUID getCustomerId()              { return customerId; }
    public void setCustomerId(UUID v)        { this.customerId = v; }
    public UUID getOperatorId()              { return operatorId; }
    public void setOperatorId(UUID v)        { this.operatorId = v; }
    public AccountType getType()             { return type; }
    public void setType(AccountType v)       { this.type = v; }
    public String getCurrency()              { return currency; }
    public void setCurrency(String v)        { this.currency = v; }
    public BigDecimal getInitialDeposit()    { return initialDeposit; }
    public void setInitialDeposit(BigDecimal v) { this.initialDeposit = v; }
    public BigDecimal getDailyLimit()        { return dailyLimit; }
    public void setDailyLimit(BigDecimal v)  { this.dailyLimit = v; }
    public BigDecimal getMonthlyLimit()      { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal v){ this.monthlyLimit = v; }
}
