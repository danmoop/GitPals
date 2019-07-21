package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RESTController
{
    @Autowired
    private UserInterface userInterface;

    /**
     * @return users' principal github object (json), containing information about them (github username, avatar image etc.)
     */
    @GetMapping("/api/principal")
    public Principal getPrincipal(Principal user)
    {
        return user;
    }

    /**
     * @return users' gitpals user object(json), containing information about them (projects, message, etc)
     */
    @GetMapping("/api/user")
    public User getUser(Principal user)
    {
        return userInterface.findByUsername(user.getName());
    }
}