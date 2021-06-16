package com.demo.bank.constant;

public enum AccountStatus {
    ACTIVATED("ACTIVATED"),
    DEACTIVATED("DEACTIVATED");

    private String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
