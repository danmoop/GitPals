package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/messages")
public class MessageAPIController {

    @Autowired
    private UserInterface userInterface;

    /**
     * @param map contains username of user whose messages will be fetched
     * @return list of user's messages
     */
    @PostMapping("/getMessages")
    public List<Message> getMessages(@RequestBody Map<String, String> map) {
        String username = map.get("username");

        if (username == null) {
            return new ArrayList<>();
        }

        User user = userInterface.findByUsername(username);

        if (user != null) {
            return user.getMessages();
        }

        return new ArrayList<>();
    }
}