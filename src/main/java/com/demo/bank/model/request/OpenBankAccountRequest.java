package com.demo.bank.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class OpenBankAccountRequest {

    @NotBlank (message = "name is invalid")
    @Pattern(regexp="^[A-Za-z- ]*$",message = "name is invalid,please input alphabet")
    @Schema(example = "Frank Gizzy")
    private String name;

    @NotBlank (message = "address is invalid")
    @Pattern(regexp="^[A-Za-z- ]*$",message = "address is invalid,please input alphabet")
    @Schema(example = "Bangkok")
    private String address;

    @NotBlank (message = "dateOfBirth is invalid")
    @Pattern(regexp = "^([0-9]{2})-([0-9]{2})-([0-9]{4})$",
            message = "dateOfBirth is invalid,please input format dd-MM-yyyy")
    @Schema(example = "21-08-1996")
    private String dateOfBirth;

    @NotBlank(message = "branchName is invalid")
    @Pattern(regexp = "^[A-Za-z- ]*$", message = "branchName is invalid,please input alphabet")
    @Schema(example = "Silom")
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
