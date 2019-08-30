package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Service.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ForumController {

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This request is handled when user wants to open forum page
     * @return forum page
     */
    @GetMapping("/forum")
    public String forumPage(Principal principal, Model model) {
        model.addAttribute("posts", forumInterface.findAll());
        model.addAttribute("user", principal);

        return "sections/forum";
    }

    /**
     * This requset is handled when user wants to open page with adding posts to forum
     * @return add form page
     */
    @GetMapping("/addForumPostForm")
    public String addForumPostForm() {
        return "sections/addForumPostForm";
    }
}