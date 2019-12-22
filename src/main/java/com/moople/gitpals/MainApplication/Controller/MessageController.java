package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    private SimpleMailMessage mailMessage = new SimpleMailMessage();


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

            List<Message> userMessages = userDB.getMessages();

            // List is reversed in order to display the latest message on the top
            // so the older message goes down
            Collections.reverse(userMessages);

            // When reversed, add this message list to html page and then display it
            model.addAttribute("userMessages", userMessages);

            return "sections/viewMessages";
        }

        return "redirect:/";
    }

    /**
     * This request is handled when user wants to send someone a message
     *
     * @return html page with a textfield for writing a message to a specific user
     */
    @GetMapping("/sendMessage")
    public String sendMessage(Model model, Principal principal) {
        // if users are not logged in - they can't send messages -> redirect them to index page
        if (principal == null) {
            return "redirect:/";
        }

        return "sections/sendMessage";
    }

    /**
     * This request is handled when user submits message sending to a specific user
     * Recipient will be found in the database and they will receive a message
     * If user is not found - display an error
     *
     * @param username & content are taken from html textfields
     * @return redirect to index page if recipient is found, otherwise redirect to error page
     **/
    @PostMapping("/messageSent")
    public String messageSent(
            Model model,
            Principal user,
            @RequestParam("RecipientName") String username,
            @RequestParam("Content") String content) {

        if (user == null) {
            return "redirect:/";
        }

        User recipient = userService.findByUsername(username);

        // If there is a user with such a username then we send them a message
        if (recipient != null) {
            Message message = new Message(user.getName(), content, Message.TYPE.INBOX_MESSAGE);

            recipient.getMessages().add(message);
            userService.save(recipient);

            /*
             * When you get a message within GitPals, you might be notified on your
             * email if you have that setting enabled
             */
            if (recipient.isNotificationsEnabled()) {
                mailMessage.setTo(recipient.getEmail());
                mailMessage.setSubject("You got a message on GitPals");
                mailMessage.setText("A message from " + user.getName() + ": " + message.getContent());
                mailSender.send(mailMessage);
            }

            return "redirect:/";
        }

        // Otherwise, notify a user that recipient is not registered, can't send them a message
        else {
            model.addAttribute("wrongRecipient", username);
            return "error/recipientNotFound";
        }

    }

    /**
     * This request is handled when user wants to delete a message in their message list
     * Message will be deleted and changed will be saved
     *
     * @param content & author are taken from hidden html textfields,
     *                which values are assigned automatically by thymeleaf
     * @return redirect to the same page - /messages
     **/
    @PostMapping("/deleteMessage")
    public String messageDeleted(
            @RequestParam("messageContentInput") String content,
            @RequestParam("messageAuthorInput") String author,
            Principal user) {
        User userDB = userService.findByUsername(user.getName());

        /*
            Message content and author are acquired from html forms,
            so if there is a match in a message list, then delete the message
         */
        Optional<Message> msg = userDB.getMessages().stream().filter(message -> message.getAuthor().equals(author) && message.getContent().equals(content)).findFirst();
        msg.ifPresent(message -> userDB.getMessages().remove(message));

        userService.save(userDB);
        return "redirect:/messages";
    }

    /**
     * This request is handled when user submits their bug
     * Message about bug will be delivered to admin
     *
     * @param message is taken from html textfield and it's content sent to admin
     * @return to index page
     **/
    @PostMapping("/reportBug")
    public String bugReported(@RequestParam("bug_description") String message, Principal user) {
        // Users can't send POST request (here - send a message) if they are not logged in
        if (user != null) {
            User admin = userService.findByUsername("danmoop");

            String author = user.getName();

            Message msg = new Message(author, message, Message.TYPE.BUG_REPORT);

            admin.getMessages().add(msg);

            userService.save(admin);
        }

        return "redirect:/";
    }

    /**
     * This request is handled when admin marks bug as fixed
     * The user who sent that bug will be notified about fix
     *
     * @param content & author are taken from hidden html textfields,
     *                which values are assigned automatically by thymeleaf
     *                It is similar to deleteMessage function, but there is an auto message that is sent to bug reporter
     * @return redirect to the same page - /messages
     **/
    @PostMapping("/bugReportFixed")
    public String bugReportFixed(
            @RequestParam("messageContentInput") String content,
            @RequestParam("messageAuthorInput") String author,
            Principal user) {

        // Manipulate with admin - remove report bug message
        User admin = userService.findByUsername(user.getName());

        Optional<Message> msg = admin.getMessages().stream().filter(message -> message.getAuthor().equals(author) && message.getContent().equals(content)).findFirst();
        msg.ifPresent(message -> admin.getMessages().remove(message));

        userService.save(admin);

        // Manipulate with bug reporter - send them a thank-you message
        User bugReportAuthor = userService.findByUsername(author);

        Message message = new Message(
                "danmoop",
                "Thanks for your previous bug report! The problem is fixed!",
                Message.TYPE.INBOX_MESSAGE
        );

        bugReportAuthor.getMessages().add(message);

        userService.save(bugReportAuthor);

        return "redirect:/messages";
    }
}