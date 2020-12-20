package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.model.Response;
import com.moople.gitpals.MainApplication.repository.ForumRepository;
import com.moople.gitpals.MainApplication.repository.KeyStorageRepository;
import com.moople.gitpals.MainApplication.repository.ProjectRepository;
import com.moople.gitpals.MainApplication.service.UserService;
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

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private KeyStorageRepository keyStorageRepository;

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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return Response.FAILED;
        }

        userService.findAll()
                .forEach(user -> {
                    user.setMobileAuthPassword("");
                    userService.save(user);
                });

        return Response.OK;
    }
}