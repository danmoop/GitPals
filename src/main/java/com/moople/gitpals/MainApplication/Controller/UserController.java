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

    /**
     * This request is handled when user wants to open another user's dashboard
     *
     * @param username is taken from an address field - like "/users/danmoop"
     * @param model    & principal are assigned automatically by spring
     * @return user's dashboard html page with all the data about the user
     **/
    @GetMapping("/users/{username}")
    public String findUser(@PathVariable String username, Model model, Principal principal) {
        User user = userService.findByUsername(username);

        if (user != null) {
            List<Project> appliedToProjects = user.getProjectsAppliedTo().stream()
                    .map(projectName -> projectInterface.findByTitle(projectName))
                    .collect(Collectors.toList());

            try {
                model.addAttribute("LoggedUser", principal.getName());
            } catch (NullPointerException e) {
                model.addAttribute("LoggedUser", null);
            }

            model.addAttribute("UserObject", userService.findByUsername(username));
            model.addAttribute("appliedProjects", appliedToProjects);

            return "sections/userDashboard";
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
    public String updateTechs(Principal user, @RequestParam("techCheckbox") List<String> techs) {
        User userFromDB = userService.findByUsername(user.getName());

        Map<String, Boolean> allTechs = userFromDB.getSkillList();

        for (Map.Entry<String, Boolean> entry : allTechs.entrySet()) {
            allTechs.put(entry.getKey(), false);
        }

        for (String item : techs) {
            if (allTechs.get(item) != null)
                allTechs.put(item, true);
        }

        userService.save(userFromDB);

        return "redirect:/dashboard";
    }


    /**
     * This request is handled when user wants to change
     * their country and bio in the dashboard
     *
     * @param country & info are taken from html input fields
     * @param user    user is used to find user in database, set new country & info
     * @return redirect to the same page with new data
     **/
    @PostMapping("/updateUserCountry")
    public String updateCountry(
            @RequestParam("countryInput") String country,
            @RequestParam("infoInput") String info,
            Principal user) {
        User userInDB = userService.findByUsername(user.getName());

        userService.save(userInDB);

        return "redirect:/dashboard";
    }
}