package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    /**
     * This request is handled when user wants to see their messages
     * All the messages are added to model and sent to user's page
     *
     * @return html page with users' messages
     */
    @GetMapping("/messages")
    public String messages(Principal user, Model model) {
        return "redirect:/";


        // if users are not logged in - they can't see any messages -> redirect them to index page
        /*if (user != null) {
            User userDB = userService.findByUsername(user.getName());
            
            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Map<String, List<Message>> messages = userDB.getDialogs();

            // When reversed, add this message list to html page and then display it
            model.addAttribute("userMessages", messages);

            return "sections/users/viewMessages";
        }

        return "redirect:/";*/
    }

    @PostMapping("/messageSent")
    public String messageSent(@RequestParam String recipientName, @RequestParam String content, Principal auth) {
        User recipient = userService.findByUsername(recipientName);

        if(auth == null || recipient == null) {
            return "redirect:/";
        }

        User sender = userService.findByUsername(auth.getName());

        List<Message> messages = sender.getDialogs().getOrDefault(recipientName, new ArrayList<>());
        messages.add(new Message(sender.getUsername(), recipientName, content, Message.TYPE.REGULAR_MESSAGE));

        sender.getDialogs().put(recipientName, messages);
        recipient.getDialogs().put(sender.getUsername(), messages);

        userService.save(sender);
        userService.save(recipient);

        return "redirect:/messages";
    }

    @GetMapping("/dialogs/{name}")
    public String dialogPage(@PathVariable String name, Principal auth, Model model) {
        if(auth == null) {
            return "redirect:/";
        }

        User user = userService.findByUsername(auth.getName());

        model.addAttribute("messages", user.getDialogs().get(name));

        return "sections/users/dialog";
    }
}