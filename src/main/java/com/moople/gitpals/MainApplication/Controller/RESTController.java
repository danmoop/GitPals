package com.moople.gitpals.MainApplication.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RESTController
{
    @GetMapping("/user")
    public Principal user(Principal user)
    {
        return user;
    }
}