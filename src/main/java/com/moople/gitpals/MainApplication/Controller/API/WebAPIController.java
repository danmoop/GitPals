package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class WebAPIController {
    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * @return users' principal github object (json), containing information about them (github username, avatar image etc.)
     */
    @GetMapping(value = "/principal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Principal getPrincipal(Principal user) {
        if (user != null)
            return user;

        return () -> "Unauthorized";
    }

    /**
     * @return users' gitpals user object(json), containing information about them (projects, message, etc)
     */
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(Principal user) {
        if (user != null)
            return userInterface.findByUsername(user.getName());

        return new User();
    }
}