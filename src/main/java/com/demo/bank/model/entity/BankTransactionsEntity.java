package com.demo.bank.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity(name = "bank_transactions")
public class BankTransactionsEntity {

    @Id
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "transaction_account_id_to")
    private UUID transactionAccountIdTo;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_date")
    private Date transactionDate;

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getTransactionAccountIdTo() {
        return transactionAccountIdTo;
    }

    public void setTransactionAccountIdTo(UUID transactionAccountIdTo) {
        this.transactionAccountIdTo = transactionAccountIdTo;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
