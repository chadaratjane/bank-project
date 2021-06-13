package com.demo.bank.model.response;

import java.math.BigDecimal;
import java.util.Date;

public class GetAllTransactionResponse {

    private Date transactionDate;
    private BigDecimal amount;
    private String transactionType;

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "GetAllTransactionResponse{" +
                "transactionDate=" + transactionDate +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}
