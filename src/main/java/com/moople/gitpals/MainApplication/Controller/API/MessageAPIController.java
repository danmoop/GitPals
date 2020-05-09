package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/messages")
public class MessageAPIController {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * This request returns a list of messages to the user
     *
     * @param token is a user's personal token
     * @return a list of messages
     */
    @GetMapping("/get/{token}")
    public List<Message> getMessages(@PathVariable String token) {
        User user = userInterface.findByUsername(jwtUtil.extractUsername(token));

        if (user == null) {
            return new ArrayList<>();
        }
        return user.getMessages();
    }

    /**
     * This request sends a message to another user
     *
     * @param data is data the sender user provides to the server, their token, recipient name, and a message itself
     * @return response whether a message was sent successfully
     */
    @PostMapping(value = "/send", produces = "application/json")
    public Response sendMessage(@RequestBody Map<Object, Object> data) {
        String recipientName = (String) data.get("recipient");
        String token = (String) data.get("token");

        LinkedHashMap linkedHashMap = (LinkedHashMap) data.get("message");
        Message message = new Message((String) linkedHashMap.get("author"), (String) linkedHashMap.get("content"), Message.TYPE.INBOX_MESSAGE);

        User recipient = userInterface.findByUsername(recipientName);
        String sender = jwtUtil.extractUsername(token);

        if (recipient == null || !sender.equals(message.getAuthor())) {
            return Response.USER_NOT_FOUND;
        }

        recipient.getMessages().add(message);
        userInterface.save(recipient);
        return Response.OK;
    }
}