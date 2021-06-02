package com.demo.bank.model.request;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class OpenBankAccountRequest {

    @NotBlank (message = "name is in valid")
    private String name;

    @NotBlank (message = "address is invalid")
    private String address;

    @NotBlank (message = "date is in valid, correct format is YYYY-MM-DD")
    private Date dateOfBirth;

    @NotBlank (message = "branchName is in valid")
    private String branchName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
