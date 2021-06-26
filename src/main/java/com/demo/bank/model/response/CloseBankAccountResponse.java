package com.demo.bank.model.response;

public class CloseBankAccountResponse {
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String accountStatus;
    private String accountBalance;

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

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return "CloseBankAccountResponse{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", branchName='" + branchName + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", accountBalance='" + accountBalance + '\'' +
                '}';
    }
}
