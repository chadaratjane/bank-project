package com.demo.bank.model.response;

import com.demo.bank.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

public class CommonResponse {

    @Schema(implementation = Status.class)
    private String status;
    private Object data;
    @JsonIgnore
    private HttpStatus httpStatus;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", httpStatus=" + httpStatus.getReasonPhrase()+
                '}';
    }
}
