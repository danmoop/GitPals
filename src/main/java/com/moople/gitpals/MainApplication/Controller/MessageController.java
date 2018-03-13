package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.security.Principal;

@Controller
public class MessageController
{
    @Autowired
    userInterface userInteface;

    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine JS = factory.getEngineByName("JavaScript");

    @GetMapping("/messages")
    public ModelAndView messages(Principal user, Model model)
    {
        User userDB = userInteface.findByUsername(user.getName());

        model.addAttribute("UserObject", userDB);

        return new ModelAndView("sections/viewMessages");
    }

    @GetMapping("/sendMessage")
    public ModelAndView sendMessage(Model model)
    {
        return new ModelAndView("sections/sendMessage");
    }

    @PostMapping("/messageSent")
    public ModelAndView messageSent(Model model, @RequestParam("RecipientName") String username, @RequestParam("Content") String content)
    {
        User recipient = userInteface.findByUsername(username);

        if(recipient != null)
        {
            Message message = new Message(recipient,content);

            User recipient2 = userInteface.findByUsername(recipient.getUsername());

            recipient2.sendMessage(message);

            userInteface.save(recipient2);

            return new ModelAndView("redirect:/");
        }

        else
        {
            model.addAttribute("wrongRecipient", username);

            return new ModelAndView("error/recipientNotFound");
        }

    }

    @PostMapping("/deleteMessage")
    public ModelAndView messageDeleted(@RequestParam("messageContentInput") String content, @RequestParam("messageAuthorInput") String author, Principal user)
    {
        User userDB = userInteface.findByUsername(user.getName());

        for(int i = 0; i < userDB.getMessages().size(); i++)
        {
            if(userDB.getMessages().get(i).getContent().equals(content) &&
                    userDB.getMessages().get(i).getAuthor().getUsername().equals(author))
            {
                userDB.deleteMessage(userDB.getMessages().get(i));
            }
        }

        userInteface.save(userDB);

        return new ModelAndView("redirect:/messages");
    }
}
