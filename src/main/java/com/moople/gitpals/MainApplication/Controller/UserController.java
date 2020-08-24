package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * This request is handled when user wants to open another user's dashboard
     *
     * @param username is taken from an address field - like "/users/danmoop"
     * @param model    & principal are assigned automatically by spring
     * @return user's dashboard html page with all the data about the user
     **/
    @GetMapping("/users/{username}")
    public String findUser(@PathVariable String username, Model model, Principal user) {
        User userDB = userService.findByUsername(username);

        if (user != null) {
            User loggedUser = userService.findByUsername(user.getName());

            if (loggedUser.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (userDB != null) {
            List<Project> appliedToProjects = userDB.getProjectsAppliedTo()
                    .stream()
                    .map(projectName -> projectInterface.findByTitle(projectName))
                    .collect(Collectors.toList());

            try {
                model.addAttribute("LoggedUser", user.getName());
            } catch (NullPointerException e) {
                model.addAttribute("LoggedUser", null);
            }

            model.addAttribute("UserObject", userService.findByUsername(username));

            return "sections/users/userDashboard";
        } else {
            model.addAttribute("user_name", username);
            return "error/userNotFound";
        }
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

        if (skills == null) {
            redirectAttributes.addFlashAttribute("error", "You should have at least one skill!");
            return "redirect:/dashboard";
        }

        skills = skills.stream()
                .filter(s -> !s.trim().equals(""))
                .collect(Collectors.toSet());

        if (user != null) {
            user.setSkillList(skills);
            userService.save(user);
        }

        return "redirect:/dashboard";
    }

    /**
     * This function removes a global message from a user's home page
     *
     * @param user is a user's authentication
     * @return to the home page
     */
    @PostMapping("/setGlobalMessageAsSeen")
    public String setGlobalMessageAsSeen(Principal user) {
        if (user == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(user.getName());
        userDB.setHasSeenGlobalMessage(true);
        userService.save(userDB);

        return "redirect:/";
    }
}