package com.demo.bank.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "bank_branches")
public class BankBranchesEntity {

    @Id
    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_city")
    private String branchCity;

    @Column(name = "branch_asset")
    private BigDecimal branchAsset;

    @Column(name = "branch_created_date")
    private Date branchCreatedDate;

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

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }

    public BigDecimal getBranchAsset() {
        return branchAsset;
    }

    public void setBranchAsset(BigDecimal branchAsset) {
        this.branchAsset = branchAsset;
    }

    public Date getBranchCreatedDate() {
        return branchCreatedDate;
    }

    public void setBranchCreatedDate(Date branchCreatedDate) {
        this.branchCreatedDate = branchCreatedDate;
    }
}
