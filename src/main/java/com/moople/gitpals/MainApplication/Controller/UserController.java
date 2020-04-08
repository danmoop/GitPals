package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.KeyStorage;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import com.nimbusds.jose.KeySourceException;
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
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    @Autowired
    private KeyStorageInterface keyStorageInterface;

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
     * This request is handled when user wants to update information about themselves (tick new languages)
     *
     * @param techs are taken from html form, they are technologies checkboxes users select in their dashboard
     * @param user  is assigned automatically by spring
     * @return redirect to the same page with new data
     **/
    @PostMapping("/updateUser")
    public String updateTechs(
            Principal user,
            @RequestParam(value = "techCheckbox", required = false) List<String> techs,
            @RequestParam(value = "notificationBool", required = false) boolean notificationBool
    ) {
        if (user == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(user.getName());

        Map<String, Boolean> allTechs = userDB.getSkillList();
        allTechs.replaceAll((k, v) -> false);

        if (techs != null) {
            for (String item : techs) {
                if (allTechs.get(item) != null) {
                    allTechs.put(item, true);
                }
            }
        }

        userDB.setNotificationsEnabled(notificationBool);

        userService.save(userDB);

        return "redirect:/dashboard";
    }

    /**
     * This request displays the user's personal key for authentication via mobile phone
     *
     * @param user is a current user authentication
     * @param attributes is where key is put so it would be seen at the user's page
     * @return dashboard page with a key
     */
    @PostMapping("/requestAuthKey")
    public String getAuthKey(Principal user, RedirectAttributes attributes) {
        String key = keyStorageInterface.findByUsername(user.getName()).getKey();

        attributes.addFlashAttribute("key", key);

        return "redirect:/dashboard";
    }

    /**
     * Tihs function reset current user's key, so a new one generated
     *
     * @param user is a current user authentication
     * @param attributes is where key is put so it would be seen at the user's page
     * @return dashboard page with a key
     */
    @PostMapping("/resetAuthKey")
    public String resetKey(Principal user, RedirectAttributes attributes) {
        KeyStorage temp = new KeyStorage(user.getName());

        KeyStorage keyStorageDB = keyStorageInterface.findByUsername(user.getName());
        keyStorageDB.setKey(temp.getKey());

        keyStorageInterface.save(keyStorageDB);

        attributes.addFlashAttribute("key", keyStorageDB.getKey());

        return "redirect:/dashboard";
    }
}