package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Pair;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
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

    @Autowired
    private SimpUserRegistry userRegistry;

    /**
     * This request is handled when user wants to see their messages
     * All the messages are added to model and sent to user's page
     *
     * @return html page with users' messages
     */
    @GetMapping("/messages")
    public String messages(Principal auth, Model model) {

        // if users are not logged in - they can't see any messages -> redirect them to index page
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Map<String, Pair<Integer, List<Message>>> dialogs = userDB.getDialogs();

            model.addAttribute("userMessages", dialogs);

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
        } else {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (userService.findByUsername(name) == null) {
            return "redirect:/users/" + name;
        }

        User user = userService.findByUsername(auth.getName());

        Pair<Integer, List<Message>> pair = user.getDialogs()
                .getOrDefault(name, new Pair<>(0, new ArrayList<>()));

        pair.setKey(0);

        userService.save(user);

        // Before user gets messages, they should be decrypted, sending a key personally to a user is unsafe
        model.addAttribute("messages", pair.getValue());

        model.addAttribute("senderName", user.getUsername());
        model.addAttribute("recipientName", name);
        model.addAttribute("key", keyStorage.findByUsername(auth.getName()).getKey());

        return "sections/users/dialog";
    }

    /**
     * This function is responsible for a sending messages in realtime
     *
     * @param message is a message a user wants to send to someone
     * @return a message object and send it to to the recipient
     */
    @MessageMapping("/messageTransmit")
    public Message message(Message message) {

        User sender = userService.findByUsername(message.getAuthor());
        User recipient = userService.findByUsername(message.getRecipient());

        // Message destination includes the user's personal key, so the message goes to the right person
        String senderDestination = "/topic/messages/" + keyStorage.findByUsername(sender.getUsername()).getKey();
        String recipientDestination = "/topic/messages/" + keyStorage.findByUsername(recipient.getUsername()).getKey();

        // Send message to both sender & recipient
        messagingTemplate.convertAndSend(recipientDestination, message);
        messagingTemplate.convertAndSend(senderDestination, message);

        // If recipient has a dialog page opened, it means they instantly read the new message (marked as 'read')
        // If recipient is not online or on the dialog page right now, the message will be marked as 'unread'
        boolean isRecipientPresent = userRegistry.findSubscriptions(subscription -> subscription
                .getDestination().equals(recipientDestination)).size() != 0;

        // The user who sends the message has it always marked as 'read', since it is outgoing
        Pair<Integer, List<Message>> pair = sender.getDialogs()
                .getOrDefault(recipient.getUsername(), new Pair<>(0, new ArrayList<>()));

        pair.getValue().add(message);
        sender.getDialogs().put(recipient.getUsername(), pair);

        userService.save(sender);

        // If recipient has the dialog page opened, the message is marked as 'read' instantly
        // If recipient is not on a dialog page, the message is marked as 'unread'

        Pair<Integer, List<Message>> pair2 = recipient.getDialogs()
                .getOrDefault(sender.getUsername(), new Pair<>(0, new ArrayList<>()));
        pair2.getValue().add(message);

        if (!isRecipientPresent) {
            pair2.setKey(pair2.getKey() + 1);
        }

        recipient.getDialogs().put(sender.getUsername(), pair2);
        userService.save(recipient);

        return message;
    }

    /**
     * ONLY FOR TESTING PURPOSES
     * TODO: Remove Later
     */
    @GetMapping("/removeAllMessages/{dialogName}")
    public String removeAllMessages(Principal auth, @PathVariable String dialogName) {
        User sender = userService.findByUsername(auth.getName());
        sender.getDialogs().remove(dialogName);

        User recipient = userService.findByUsername(dialogName);
        recipient.getDialogs().remove(auth.getName());

        userService.save(sender);
        userService.save(recipient);

        return "redirect:/dialogs/" + dialogName;
    }
}