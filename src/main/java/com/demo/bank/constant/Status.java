package com.demo.bank.constant;

public enum Status {
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    NOT_FOUND("NOT_FOUND"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    BAD_REQUEST("BAD_REQUEST"),
    DATA_ACCESS_FAILED("DATA_ACCESS_FAILED");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


