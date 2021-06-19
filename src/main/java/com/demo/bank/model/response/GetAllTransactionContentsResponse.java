package com.demo.bank.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.math.BigDecimal;
import java.util.Date;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAllTransactionContentsResponse {

    private Date transactionDate;
    private BigDecimal amount;
    private String transactionType;
    private String receiverAccountNumber;
    private String receiverAccountName;

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

    public String getReceiverAccountName() {
        return receiverAccountName;
    }

    public void setReceiverAccountName(String receiverAccountName) {
        this.receiverAccountName = receiverAccountName;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    @Override
    public String toString() {
        return "GetAllTransactionContentsResponse{" +
                "transactionDate=" + transactionDate +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                ", receiverAccountNumber='" + receiverAccountNumber + '\'' +
                ", receiverAccountName='" + receiverAccountName + '\'' +
                '}';
    }
}
