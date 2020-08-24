package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private KeyStorageInterface keyStorage;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * This request is handled when user wants to see their messages
     * All the messages are added to model and sent to user's page
     *
     * @return html page with users' messages
     */
    @GetMapping("/messages")
    public String messages(Principal user, Model model) {

        // if users are not logged in - they can't see any messages -> redirect them to index page
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Map<String, List<Message>> messages = userDB.getDialogs();

            // When reversed, add this message list to html page and then display it
            model.addAttribute("userMessages", messages);

            return "sections/users/viewMessages";
        }

        return "redirect:/";
    }

    /**
     * This function redirects to a dialog with a user for the consequent messaging
     *
     * @param dialogLink is a link, which contains recipient's username, so the proper dialog opened
     * @return to the dialog page
     */
    @PostMapping("/openDialog")
    public String openDialog(@RequestParam String dialogLink) {
        return "redirect:/dialogs/" + dialogLink;
    }

    /**
     * This function opens a dialog with a user for the consequent messaging
     *
     * @param name  is a username of a person you want send a message to
     * @param auth  is a sender's current authentication
     * @param model is where all the previous message are put along with information about two users (sender/recipient)
     * @return to the dialog page, in which every user can message each other
     */
    @GetMapping("/dialogs/{name}")
    public String dialogPage(@PathVariable String name, Principal auth, Model model) {
        if (auth == null) {
            return "redirect:/";
        }

        if(userService.findByUsername(name) == null) {
            return "redirect:/users/" + name;
        }

        User user = userService.findByUsername(auth.getName());

        List<Message> messages = user.getDialogs().getOrDefault(name, new ArrayList<>());

        model.addAttribute("messages", messages);
        model.addAttribute("senderName", user.getUsername());
        model.addAttribute("recipientName", name);
        model.addAttribute("key", keyStorage.findByUsername(auth.getName()).getKey());

        return "sections/users/dialog";
    }

    /**
     * This function is responsible for a sending messages in realtime
     *
     * @param message is a message a user wants to send to someone
     * @param auth    is a sender's user authentication
     * @return a message object and send it to to the recipient
     */
    @MessageMapping("/messageTransmit")
    public Message message(Message message, Principal auth) {

        User sender = userService.findByUsername(message.getAuthor());
        User recipient = userService.findByUsername(message.getRecipient());

        String senderDestination = "/topic/messages/" + keyStorage.findByUsername(sender.getUsername()).getKey();
        String recipientDestination = "/topic/messages/" + keyStorage.findByUsername(recipient.getUsername()).getKey();

        messagingTemplate.convertAndSend(senderDestination, message);
        messagingTemplate.convertAndSend(recipientDestination, message);

        List<Message> messages = sender.getDialogs().getOrDefault(recipient.getUsername(), new ArrayList<>());
        messages.add(message);

        sender.getDialogs().put(recipient.getUsername(), messages);
        recipient.getDialogs().put(sender.getUsername(), messages);

        userService.save(sender);
        userService.save(recipient);

        return message;
    }
}