package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.model.Message;
import com.moople.gitpals.MainApplication.model.Pair;
import com.moople.gitpals.MainApplication.model.Response;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.ForumService;
import com.moople.gitpals.MainApplication.service.KeyStorageService;
import com.moople.gitpals.MainApplication.service.UserService;
import com.moople.gitpals.MainApplication.tools.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private ForumService projectService;

    @Autowired
    private KeyStorageService keyStorageService;

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

        List<User> users = userService.findAll();

        for (int i = 0; i < users.size(); i++) {
            Map<String, Pair<Integer, List<Message>>> dialogs = users.get(i).getDialogs();

            for (Map.Entry<String, Pair<Integer, List<Message>>> entry : dialogs.entrySet()) {
                String recipient = entry.getKey();
                Pair<Integer, List<Message>> pair = entry.getValue();
                List<Message> messages = pair.getValue();

                messages.forEach(msg -> msg.setContent(Encrypt.simpleEncrypt(msg.getContent())));
                pair.setValue(messages);
                dialogs.put(recipient, pair);
            }

            users.get(i).setDialogs(dialogs);
        }

        return Response.OK;
    }
}