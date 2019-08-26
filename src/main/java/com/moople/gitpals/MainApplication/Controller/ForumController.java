package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Service.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String forumPage(Principal principal) {
        return "sections/forum";
    }
}