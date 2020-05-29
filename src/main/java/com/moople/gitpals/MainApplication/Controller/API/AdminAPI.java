package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private ForumInterface forumInterface;

    @Autowired
    private ProjectInterface projectInterface;

    @Autowired
    private KeyStorageInterface keyStorageInterface;

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
        if (admin == null || !admin.getName().equals("danmoop")) {
            return Response.FAILED;
        }

        // Example, in which comments had no "edited" boolean and "key" parameter,
        // so I had to update database manually by calling this request

        /*forumInterface.findAll()
                .forEach(post -> {
                    post.getComments().forEach(comment -> {
                        comment.setKey(comment.generateKey());
                        comment.setEdited(false);
                    });
                    forumInterface.save(post);
                });*/

        return Response.OK;
    }

    /**
     * This request creates a database backup, so it could be restored if something goes wrong
     *
     * @param admin is admin's authentication, so no other user can get this information
     * @return project database backup
     */
    @GetMapping(value = "/backupDB", produces = "application/json")
    public List<List<?>> backUp(Principal admin) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return new ArrayList<>();
        }

        List<List<?>> backup = new ArrayList<>();

        backup.add(userService.findAll());
        backup.add(projectInterface.findAll());
        backup.add(forumInterface.findAll());
        backup.add(keyStorageInterface.findAll());

        return backup;
    }
}