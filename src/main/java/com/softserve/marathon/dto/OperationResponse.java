package com.softserve.marathon.dto;

public class OperationResponse {

    private String status;

    public OperationResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
