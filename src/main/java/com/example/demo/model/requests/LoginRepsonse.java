package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRepsonse {
    @JsonProperty
    String token;

    public String getToken() {
        return token;
    }

    public LoginRepsonse(String token) {
        this.token = token;
    }
}
