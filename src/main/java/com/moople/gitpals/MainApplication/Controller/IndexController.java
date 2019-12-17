package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * This request is handled when user opens index page
     * Add attributes about user and later display them on the page
     *
     * @return html index page with a list of projects and TECHS
     */
    @GetMapping("/")
    public String indexPage(OAuth2Authentication user, Model model) {
        // If we are logged in, display information about us on the index page
        if (user != null) {
            LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) user.getUserAuthentication().getDetails();

            model.addAttribute("GithubUser", user);

            // If we are logged in but there is no our user object in database, save it
            // Usually this function is executed once when we are register for the first time
            if (userService.findByUsername(user.getName()) == null) {
                userService.save(
                        new User(
                                user.getName(),
                                "https://github.com/" + user.getName(),
                                Data.technologiesMap,
                                properties.get("email").toString()
                        )
                );
            }

            model.addAttribute("userDB", userService.findByUsername(user.getName()));
        }

        int projectsAmount = projectInterface.findAll().size();
        List<Project> projects;

        if(projectsAmount <= 50) {
            projects = projectInterface.findAll();
        } else {
            // Show the most recent projects (only 50)
            projects = projectInterface.findAll()
                    .stream()
                    .skip(projectInterface.findAll().size() - 50)
                    .collect(Collectors.toList());
        }

        Collections.reverse(projects);

        model.addAttribute("projectTechs", Data.technologiesMap);
        model.addAttribute("projects", projects);
        model.addAttribute("totalProjectsAmount", projectsAmount);
        model.addAttribute("usersRegistered", userService.findAll().size());

        return "sections/index";
    }

    /**
     * This request is handled when user opens their dashboard page
     * Add attributes about user and later display them on the page
     *
     * @return html page with user's principal data (username, etc)
     */
    @GetMapping("/dashboard")
    public String dashboardPage(Principal user, Model model) {
        // if user is not logged in - redirect to index
        if (user == null) {
            return "redirect:/";
        }

        // user is logged in
        else {
            User userDB = userService.findByUsername(user.getName());

            model.addAttribute("dbUser", userDB);
            model.addAttribute("userObject", new User());
            model.addAttribute("GithubUser", user);

            List<Project> appliedToProjects = userDB.getProjectsAppliedTo()
                    .stream()
                    .map(projectName -> projectInterface.findByTitle(projectName))
                    .collect(Collectors.toList());

            model.addAttribute("appliedProjects", appliedToProjects);

            return "sections/dashboard";
        }
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

    /**
     * This request is handled when user wants to submit a bug
     *
     * @return html page where users can report about a bug
     */
    @GetMapping("/bugReport")
    public String bugReport() {
        return "sections/bugReport";
    }

    /**
     * This request is handled when user wants to see a guide about submitting a project
     *
     * @return html page where users can read some advices about submitting a project
     */
    @GetMapping("/guide/how-to-create-a-good-description-for-my-project")
    public String goodDescriptionGuidwe() {
        return "guide/goodProjDescription";
    }
}
