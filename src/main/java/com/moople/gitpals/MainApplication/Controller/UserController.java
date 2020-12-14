package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Pair;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Encrypt;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * This request is handled when user opens their dashboard page
     * Add attributes about user and later display them on the page
     *
     * @return html page with user's principal data (username, etc)
     */
    @GetMapping("/dashboard")
    public String dashboardPage(Principal auth, Model model) {

        // if user is not logged in - redirect to home page
        if (auth == null) {
            return "redirect:/";
        }

        // user is logged in
        else {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            model.addAttribute("userDB", userDB);

            return "sections/users/dashboard";
        }
    }

    /**
     * This request is handled when user wants to open another user's dashboard
     *
     * @param username is taken from an address field - like "/users/danmoop"
     * @param model    & principal are assigned automatically by spring
     * @return user's dashboard html page with all the data about the user
     **/
    @GetMapping("/users/{username}")
    public String findUser(@PathVariable String username, Model model, Principal auth) {
        User userDB = userService.findByUsername(username);

        if (auth != null) {
            User loggedUser = userService.findByUsername(auth.getName());

            if (loggedUser.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (userDB != null) {
            userDB.setNotifications(null);
            userDB.setDialogs(null);

            model.addAttribute("LoggedUser", auth != null ? auth.getName() : null);
            model.addAttribute("UserObject", userDB);

            return "sections/users/userDashboard";
        } else {
            model.addAttribute("user_name", username);
            return "error/userNotFound";
        }
    }

    /**
     * This request redirect user to their notification page where they can see all the notifications
     *
     * @param auth  is a user's authentication object
     * @param model is where the notification list is put so it would be displayed on the user's page
     * @return notification page
     */
    @GetMapping("/notifications")
    public String openNotificationsPage(Principal auth, Model model) {

        User userDB = userService.findByUsername(auth.getName());

        if (userDB == null) {
            return "redirect:/";
        } else if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        userDB.getNotifications().setKey(0);
        userService.save(userDB);

        model.addAttribute("notifications", userDB.getNotifications().getValue());

        return "sections/users/notifications";
    }

    /**
     * This request removes a notification
     *
     * @param auth            is a user's authentication object
     * @param notificationKey is a unique key so we could find the notification in O(1)
     * @return notification page with changes made
     */
    @PostMapping("/removeNotification")
    public String removeNotification(Principal auth, @RequestParam String notificationKey) {
        User userDB = userService.findByUsername(auth.getName());

        userDB.getNotifications().getValue().remove(notificationKey);
        userService.save(userDB);

        return "redirect:/notifications";
    }


    /**
     * This request removes all user's notification
     *
     * @param auth is a user's authentication object
     * @return notification page with all the notifications removed
     */
    @PostMapping("/removeAllNotifications")
    public String removeAllNotifications(Principal auth) {
        User userDB = userService.findByUsername(auth.getName());

        userDB.setNotifications(new Pair<>(0, new HashMap<>()));
        userService.save(userDB);

        return "redirect:/notifications";
    }

    /**
     * This request is handled when user wants to update information about themselves (pick new languages)
     *
     * @param auth   is a current user's authentication
     * @param skills is a new or updated set of skills
     * @return a dashboard page with new skills
     */
    @PostMapping("/updateUser")
    public String updateTechs(Principal auth, @RequestParam(value = "skill", required = false) Set<String> skills, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(auth.getName());

        if (skills == null || skills.size() == 0) {
            redirectAttributes.addFlashAttribute("error", "You should have at least one skill!");
            return "redirect:/dashboard";
        }

        skills = skills.stream()
                .filter(s -> !s.trim().equals(""))
                .collect(Collectors.toSet());

        if (skills.size() == 0) {
            redirectAttributes.addFlashAttribute("error", "You should have at least one skill!");
            return "redirect:/dashboard";
        }

        if (user != null) {
            user.setSkillList(skills);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "Saved!");

        return "redirect:/dashboard";
    }

    /**
     * This function sets a mobile app auth password for the user
     * They can use the password to sign in using mobile app
     * Password is stored in md5 format
     *
     * @param auth     is user's authentication object
     * @param password is a password which will be set instead of the old one (if exists)
     * @return dashboard page
     */
    @PostMapping("/setMobileAuthPassword")
    public String setMobileAuthPassword(Principal auth, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(auth.getName());

        user.setMobileAuthPassword(Encrypt.MD5(password));
        userService.save(user);

        redirectAttributes.addFlashAttribute("message", "Saved!");
        return "redirect:/dashboard";
    }

    /**
     * This function removes a global message from a user's home page
     *
     * @param auth is a user's authentication
     * @return to the home page
     */
    @PostMapping("/setGlobalMessageAsSeen")
    public String setGlobalMessageAsSeen(Principal auth) {
        if (auth == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(auth.getName());
        userDB.setHasSeenGlobalMessage(true);
        userService.save(userDB);

        return "redirect:/";
    }
}