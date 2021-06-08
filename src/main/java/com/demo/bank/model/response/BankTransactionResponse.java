package com.demo.bank.model.response;

import java.math.BigDecimal;
import java.util.Date;

public class BankTransactionResponse {
    String accountName;
    String accountNumber;
    BigDecimal amount;
    BigDecimal accountBalance;
    Date transactionDate;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "BankTransactionResponse{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + "XXXXX" + accountNumber.substring(5,10)+ '\'' +
                ", amount=" + amount +
                ", accountBalance=" + accountBalance +
                ", transactionDate=" + transactionDate +
                '}';
    }
}

