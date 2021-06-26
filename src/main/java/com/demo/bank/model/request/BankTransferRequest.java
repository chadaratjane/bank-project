package com.demo.bank.model.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class BankTransferRequest {

    @NotBlank(message = "senderAccountNumber is invalid")
    @Pattern(regexp = "^[0-9]{10}$", message = "senderAccountNumber is invalid,please input number with 10 digit numbers")
    private String senderAccountNumber;

    @NotBlank(message = "receiverAccountNumber is invalid")
    @Pattern(regexp = "^[0-9]{10}$", message = "receiverAccountNumberTo is invalid,please input number with 10 digit numbers")
    private String receiverAccountNumber;

    @NotNull(message = "amount is invalid")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "amount is invalid,please input positive amount")
    @Digits(integer = 10, fraction = 2,
            message = "amount is invalid,please input not more than 10 digits with 2 decimal places")
    private BigDecimal amount;

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

    @Override
    public String toString() {
        return "BankTransferRequest{" +
                ", senderAccountNumber='" + senderAccountNumber+ '\'' +
                ", receiverAccountNumber='" + receiverAccountNumber+ '\'' +
                ", amount=" + amount +
                '}';
    }
}
