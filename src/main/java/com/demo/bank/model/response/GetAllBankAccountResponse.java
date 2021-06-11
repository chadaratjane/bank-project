package com.demo.bank.model.response;

import java.math.BigDecimal;

public class GetAllBankAccountResponse {
    private String accountName;
    private String accountNumber;
    private String branchName;
    private BigDecimal accountBalance;

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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return "GetAllBankAccountResponse{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + "XXXXX" + accountNumber.substring(5,10)+ '\'' +
                ", branchName='" + branchName + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
