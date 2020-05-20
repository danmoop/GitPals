package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
public class AdminAPI {

    @Autowired
    private UserService userService;

    /**
     * This function is only for admin
     * It performs some manipulations with user DB
     * When User.java gets new parameter, this function adds it to all the users in DB
     *
     * @param admin is an admin authentication
     * @return a response whether changes were made
     */
    @GetMapping("/set")
    public Response setter(Principal admin) {
        if (!admin.getName().equals("danmoop")) {
            return Response.FAILED;
        }

        userService.findAll().forEach(user -> {
            user.setBanned(false);
            userService.save(user);
        });

        return Response.OK;
    }
}