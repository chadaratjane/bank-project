package com.demo.bank.model.response;

import java.util.UUID;

public class BankResponseOpenBankAccount {

    private String accountName;
    private String accountNumber;
    private Integer branchId;
    private String branchName;

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

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return "BankResponseOpenBankAccount{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", branchId=" + branchId +
                ", branchName='" + branchName + '\'' +
                '}';
    }
}
