package com.demo.bank.model.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class BankTransactionRequest {

    @NotBlank(message = "accountNumber is invalid")
    @Pattern(regexp = "^[0-9]{10}$", message = "accountNumber is invalid,please input number with 10 digit numbers")
    private String accountNumber;

    @NotNull(message = "amount is invalid")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "amount is invalid,please input positive amount")
    @Digits(integer = 10, fraction = 2,
            message = "amount is invalid,please input not more than 10 digits with 2 decimal places")
    private BigDecimal amount;


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

    @Override
    public String toString() {
        return "BankResponseDepositWithdrawTransaction{" +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
