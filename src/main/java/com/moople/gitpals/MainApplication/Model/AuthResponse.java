package com.moople.gitpals.MainApplication.Model;

import java.io.Serializable;

public class AuthResponse implements Serializable {

    private final String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}