package com.demo.bank.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity(name = "bank_accounts")
public class BankAccountsEntity {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "account_branch_id")
    private Integer accountBranchId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_balance")
    private BigDecimal accountBalance;

    @Column(name = "account_status")
    private String accountStatus;

    @Column(name = "account_created_date")
    private Date accountCreatedDate;

    @Column(name = "account_updated_date")
    private Date accountUpdatedDate;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Integer getAccountBranchId() {
        return accountBranchId;
    }

    public void setAccountBranchId(Integer accountBranchId) {
        this.accountBranchId = accountBranchId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Date getAccountCreatedDate() {
        return accountCreatedDate;
    }

    public void setAccountCreatedDate(Date accountCreatedDate) {
        this.accountCreatedDate = accountCreatedDate;
    }

    public Date getAccountUpdatedDate() {
        return accountUpdatedDate;
    }

    public void setAccountUpdatedDate(Date accountUpdatedDate) {
        this.accountUpdatedDate = accountUpdatedDate;
    }
}
