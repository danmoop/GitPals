package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuthResponse implements Serializable {

    private final String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}