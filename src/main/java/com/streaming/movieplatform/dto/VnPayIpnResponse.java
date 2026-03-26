package com.streaming.movieplatform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VnPayIpnResponse {

    @JsonProperty("RspCode")
    private final String rspCode;

    @JsonProperty("Message")
    private final String message;

    public VnPayIpnResponse(String rspCode, String message) {
        this.rspCode = rspCode;
        this.message = message;
    }

    public String getRspCode() {
        return rspCode;
    }

    public String getMessage() {
        return message;
    }
}
