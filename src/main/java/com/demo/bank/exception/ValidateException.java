package com.demo.bank.exception;

public class ValidateException extends RuntimeException {
    private String errorCause;
    private String errorMessage;

    public ValidateException(String errorCause, String errorMessage){
        this.errorCause = errorCause;
        this.errorMessage = errorMessage;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
