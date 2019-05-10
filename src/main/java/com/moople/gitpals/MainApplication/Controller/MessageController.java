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
    private UserInterface userInteface;

    @GetMapping("/messages")
    public String messages(Principal user, Model model)
    {
        User userDB = userInteface.findByUsername(user.getName());

        model.addAttribute("UserObject", userDB);

        return "sections/viewMessages";
    }

    // Redirects to page where you can send a message
    @GetMapping("/sendMessage")
    public String sendMessage(Model model)
    {
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
        String recipient = username;

        if(recipient != null)
        {
            Message message = new Message(recipient,content);

            User recipient2 = userInteface.findByUsername(recipient);

            recipient2.sendMessage(message);

            userInteface.save(recipient2);

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
        User userDB = userInteface.findByUsername(user.getName());

        for(int i = 0; i < userDB.getMessages().size(); i++)
        {
            if(userDB.getMessages().get(i).getContent().equals(content) &&
                    userDB.getMessages().get(i).getAuthor().equals(author))
            {
                userDB.deleteMessage(userDB.getMessages().get(i));
            }
        }

        userInteface.save(userDB);

        return "redirect:/messages";
    }
}