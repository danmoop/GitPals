package com.moople.gitpals.MainApplication.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RESTController
{
    /**
     * @return users' principal object (json), containing information about them (github username, avatar image etc.)
     */
    @GetMapping("/user")
    public Principal user(Principal user)
    {
        return user;
    }
}