package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class MessageController
{
    @Autowired
    private UserInterface userInterface;

    /**
     * @return html page with users' messages
     */
    @GetMapping("/messages")
    public String messages(Principal user, Model model)
    {
        // if users are not logged in - they can't see any messages -> redirect them to index page
        if(user != null)
        {
            User userDB = userInterface.findByUsername(user.getName());

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
     * @return html page with a textfield for writing a message to a specific user
     */
    @GetMapping("/sendMessage")
    public String sendMessage(Model model, Principal principal)
    {
        // if users are not logged in - they can't send messages -> redirect them to index page
        if(principal == null)
            return "redirect:/";

        return "sections/sendMessage";
    }

    /**
     * @param username & content are taken from html textfields
     * @return redirect to index page if recipient is found, otherwise redirect to error page
     **/
    @PostMapping("/messageSent")
    public String messageSent(
            Model model,
            @RequestParam("RecipientName") String username,
            @RequestParam("Content") String content)
    {
        // If there is a user with such a username then we send them a message
        if(userInterface.findByUsername(username) != null)
        {
            Message message = new Message(username, content);

            User recipient = userInterface.findByUsername(username);

            recipient.getMessages().add(message);

            userInterface.save(recipient);

            return "redirect:/";
        }

        // Otherwise, notify a user that recipient is not registered, can't send them a message
        else
        {
            model.addAttribute("wrongRecipient", username);

            return "error/recipientNotFound";
        }

    }

    /**
     * @param content & author are taken from hidden html textfields,
        which values are assigned automatically by thymeleaf

     * @return redirect to the same page - /messages
     **/
    @PostMapping("/deleteMessage")
    public String messageDeleted(
            @RequestParam("messageContentInput") String content,
            @RequestParam("messageAuthorInput") String author,
            Principal user)
    {
        User userDB = userInterface.findByUsername(user.getName());

        /*
            Message content and author are acquired from html forms,
            so if there is a match in a message list, then delete the message
         */
        userDB.getMessages().removeIf(message -> message.getAuthor().equals(author) && message.getContent().equals(content));

        userInterface.save(userDB);

        return "redirect:/messages";
    }

    /**
     * @param message is taken from html textfield and it's content sent to admin
     * @return to index page
     **/
    @PostMapping("/reportBug")
    public String bugReported(@RequestParam("bug_description") String message, Principal user)
    {
        // Users can't send POST request (here - send a message) if they are not logged in
        if (user != null)
        {
            User admin = userInterface.findByUsername("danmoop");

            String author = user.getName();

            Message msg = new Message(
                    author,
                    message
            );

            msg.setBugReport(true);

            admin.getMessages().add(msg);

            userInterface.save(admin);
        }

        return "redirect:/";
    }

    /**
     * @param content & author are taken from hidden html textfields,
        which values are assigned automatically by thymeleaf

        It is similar to deleteMessage function, but there is an
        auto message that is sent to bug reporter

     * @return redirect to the same page - /messages
     **/
    @PostMapping("/bugReportFixed")
    public String bugReportFixed(
            @RequestParam("messageContentInput") String content,
            @RequestParam("messageAuthorInput") String author,
            Principal user)
    {
        // Manipulate with admin - remove report bug message

        User admin = userInterface.findByUsername(user.getName());
        admin.getMessages().removeIf(message -> message.getAuthor().equals(author) && message.getContent().equals(content));
        userInterface.save(admin);


        // Manipulate with bug reporter - send them a thank-you message

        User bugReportAuthor = userInterface.findByUsername(author);

        Message msg = new Message(
                "danmoop",
                "Thanks for your previous bug report! The problem is fixed!"
        );

        bugReportAuthor.getMessages().add(msg);

        userInterface.save(bugReportAuthor);

        return "redirect:/messages";
    }
}