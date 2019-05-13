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

@Controller
public class MessageController
{
    @Autowired
    private UserInterface userInterface;

    @GetMapping("/messages")
    public String messages(Principal user, Model model)
    {
        // if users are not logged in - they can't see any messages -> redirect them to index page
        if(user != null)
        {
            User userDB = userInterface.findByUsername(user.getName());

            model.addAttribute("UserObject", userDB);

            return "sections/viewMessages";
        }

        return "redirect:/";
    }

    // Redirects to page where you can send a message
    @GetMapping("/sendMessage")
    public String sendMessage(Model model, Principal principal)
    {
        // if users are not logged in - they can't send messages -> redirect them to index page
        if(principal == null)
            return "redirect:/";

        return "sections/sendMessage";
    }

    /*
        @param username & content are taken from html textfields
        @return redirect to index page if recipient is found, otherwise redirect to error page
     */
    @PostMapping("/messageSent")
    public String messageSent(
            Model model,
            @RequestParam("RecipientName") String username,
            @RequestParam("Content") String content)
    {

        if(userInterface.findByUsername(username) != null)
        {
            Message message = new Message(username,content);

            User recipient2 = userInterface.findByUsername(username);

            recipient2.sendMessage(message);

            userInterface.save(recipient2);

            return "redirect:/";
        }

        else
        {
            model.addAttribute("wrongRecipient", username);

            return "error/recipientNotFound";
        }

    }

    /*
        @param content & author are taken from hidden html textfields,
            which values are assigned automatically by thymeleaf

        @return redirect to the same page - /messages
     */
    @PostMapping("/deleteMessage")
    public String messageDeleted(
            @RequestParam("messageContentInput") String content,
            @RequestParam("messageAuthorInput") String author,
            Principal user)
    {
        User userDB = userInterface.findByUsername(user.getName());

        for(int i = 0; i < userDB.getMessages().size(); i++)
        {
            if(userDB.getMessages().get(i).getContent().equals(content) &&
                    userDB.getMessages().get(i).getAuthor().equals(author))
            {
                userDB.deleteMessage(userDB.getMessages().get(i));
            }
        }

        userInterface.save(userDB);

        return "redirect:/messages";
    }

    /*
        @param message is taken from html textfield and it's content sent to admin
        @return to index page
     */
    @PostMapping("/reportBug")
    public String bugReported(@RequestParam("bug_description") String message, Principal user)
    {
        User admin = userInterface.findByUsername("danmoop");

        String author = user.getName();

        Message msg = new Message(
                author,
                message
        );

        admin.sendMessage(msg);

        userInterface.save(admin);

        return "redirect:/";
    }
}