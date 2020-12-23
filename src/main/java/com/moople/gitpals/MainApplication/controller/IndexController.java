package com.moople.gitpals.MainApplication.controller;

import com.moople.gitpals.MainApplication.model.GlobalMessage;
import com.moople.gitpals.MainApplication.model.KeyStorage;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private KeyStorageService keyStorageService;

    @Autowired
    private GlobalMessageService globalMessageService;

    @GetMapping("/")
    public String index() {
        return "redirect:/page/1";
    }

    /**
     * This request is handled when user opens index page
     * Add attributes about user and later display them on the page
     *
     * @return html index page with a list of projects and TECHS
     */
    @GetMapping("/page/{page}")
    public String indexPage(OAuth2Authentication auth, Model model, RedirectAttributes redirectAttributes, @PathVariable(value = "page", required = false) int page) {

        int numberOfPages = indexService.getNumberOfPages();

        // Check if the user is not trying to open a page, which doesn't exist
        if (page > numberOfPages) {
            return "redirect:/page/" + numberOfPages;
        } else if (page < 1) {
            return "redirect:/page/1";
        }

        // If we are logged in, display information about us on the index page
        if (auth != null) {
            LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) auth.getUserAuthentication().getDetails();

            // Extract information about the user from their GitHub account
            String email = properties.get("email") == null ? null : properties.get("email").toString();
            String country = properties.get("location") == null ? null : properties.get("location").toString();
            String bio = properties.get("bio") == null ? null : properties.get("bio").toString();
            String avatarURL = properties.get("avatar_url").toString();

            /*
             *  When authentication exists, however, there is no such user in the database,
             *  it means that this user has just logged in for the first time
             */
            if (userService.findByUsername(auth.getName()) == null) {
                User newRegisteredUser = new User(auth.getName(), "https://github.com/" + auth.getName(), email, country, bio, avatarURL);

                userService.save(newRegisteredUser);
                keyStorageService.save(new KeyStorage(auth.getName()));

                redirectAttributes.addFlashAttribute("message", "You have just registered! Fill in the information about yourself - choose skills you know on this page!");
                return "redirect:/dashboard";
            }

            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            } else if (userDB.getSkillList().size() == 0) {
                redirectAttributes.addFlashAttribute("error", "You should have at least one skill!");
                return "redirect:/dashboard";
            }

            /*
            // TODO: uncomment when mobile version of GitPals is finished
            else if (userDB.getMobileAuthPassword().equals("")) {
                redirectAttributes.addFlashAttribute("error", "You should set up your mobile app auth password!");
                return "redirect:/dashboard";
            }*/

            indexService.checkIfDataHasChanged(userDB, properties);

            model.addAttribute("userDB", userDB);
            model.addAttribute("unreadMessages", indexService.countUnreadMessages(userDB));
        }

        List<Project> projects = indexService.getProjectsOnPage(page);

        List<GlobalMessage> globalMessages = globalMessageService.findAll();
        if (globalMessages.size() != 0) {
            model.addAttribute("globalMessage", globalMessages.get(0));
        }

        model.addAttribute("projects", projects);
        model.addAttribute("pagesLength", numberOfPages);
        model.addAttribute("page", page);

        return "sections/users/index";
    }

    /**
     * This request is handled when user wants to log out
     * Session will be cleared
     *
     * @return html index page with logged-out user
     */
    @GetMapping("/signout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }
}