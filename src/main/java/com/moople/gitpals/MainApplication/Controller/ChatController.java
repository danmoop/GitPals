package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Message message(Message message) {
        System.out.println(message.toString());
        return message;
    }

    @GetMapping("/chat")
    public String chatPage(Principal auth, Model model) {
        if(auth != null) {
            model.addAttribute("user", userService.findByUsername(auth.getName()));
            return "sections/chat";
        } else {
            return "redirect:/";
        }
    }
}