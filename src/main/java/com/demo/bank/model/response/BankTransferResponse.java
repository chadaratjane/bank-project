package com.demo.bank.model.response;

import java.math.BigDecimal;
import java.util.Date;

public class BankTransferResponse {

    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private  BigDecimal SenderAccountBalance;
    private Date transactionDate;

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSenderAccountBalance() {
        return SenderAccountBalance;
    }

    public void setSenderAccountBalance(BigDecimal senderAccountBalance) {
        SenderAccountBalance = senderAccountBalance;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "BankTransferResponse{" +
                "senderAccountNumber='" + "XXXXX" +senderAccountNumber.substring(5,10) + '\'' +
                ", receiverAccountNumber='" + "XXXXX" + receiverAccountNumber.substring(5,10) + '\'' +
                ", amount=" + amount +
                ", SenderAccountBalance=" + SenderAccountBalance +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
