package com.demo.bank.model.response;

public class CloseBankAccountResponse {
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String accountStatus;

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

    @Override
    public String toString() {
        return "CloseBankAccountResponse{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + "XXXXX" + accountNumber.substring(5,10)+ '\'' +
                ", branchName='" + branchName + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                '}';
    }
}
