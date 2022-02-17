package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.Response;
import com.moople.gitpals.MainApplication.service.ForumService;
import com.moople.gitpals.MainApplication.service.KeyStorageService;
import com.moople.gitpals.MainApplication.service.ProjectService;
import com.moople.gitpals.MainApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashSet;

@RestController
@RequestMapping("/api/admin")
public class AdminAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private KeyStorageService keyStorageService;

    /**
     * This function is only for admins
     * It performs some manipulations with user DB
     * When User.java gets a new parameter, this function adds it to all the users in DB
     *
     * @param admin is an admin authentication
     * @return a response whether changes were made
     */
    @GetMapping("/set")
    public Response setter(Principal admin) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return Response.FAILED;
        }

        for (Project p : projectService.findAll()) {
            p.setLikes(new HashSet<>());
            projectService.save(p);
        }

        return Response.OK;
    }
}