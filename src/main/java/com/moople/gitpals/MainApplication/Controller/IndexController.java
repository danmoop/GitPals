package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.KeyStorage;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    @Autowired
    private ForumInterface forumInterface;

    @Autowired
    private KeyStorageInterface keyStorageInterface;

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

            String email = properties.get("email") == null ? null : properties.get("email").toString();
            String country = properties.get("location") == null ? null : properties.get("location").toString();
            String bio = properties.get("bio") == null ? null : properties.get("bio").toString();

            if (userService.findByUsername(user.getName()) == null) {
                userService.save(
                        new User(
                                user.getName(),
                                "https://github.com/" + user.getName(),
                                Data.technologiesMap,
                                email,
                                country,
                                bio
                        )
                );
            }

            if (keyStorageInterface.findByUsername(user.getName()) == null) {
                keyStorageInterface.save(new KeyStorage(user.getName()));
            }

            User userDB = userService.findByUsername(user.getName());
            checkIfDataHasChanged(userDB, properties);

            model.addAttribute("userDB", userDB);
        }

        List<Project> allProjects = projectInterface.findAll();
        int projectsAmount = allProjects.size();

        List<Project> projects;

        if (projectsAmount <= 50) {
            projects = allProjects;
        } else {
            projects = new ArrayList<>();

            for (int i = projectsAmount - 1; i >= projectsAmount - 50; i--) {
                projects.add(allProjects.get(i));
            }
        }

        model.addAttribute("projectTechs", Data.technologiesMap);
        model.addAttribute("projects", projects);
        model.addAttribute("totalProjectsAmount", projectsAmount);
        model.addAttribute("forumPostsSize", forumInterface.findAll().size());
        model.addAttribute("usersRegistered", userService.findAll().size());

        return "sections/users/index";
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

            return "sections/users/dashboard";
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

    private void checkIfDataHasChanged(User userDB, LinkedHashMap<String, Object> properties) {

        // Email
        if (properties.get("email") == null) {
            if (userDB.getEmail() != null) {
                userDB.setEmail(null);
                userService.save(userDB);
            }
        } else {
            if (userDB.getEmail() == null || !userDB.getEmail().equals(properties.get("email").toString())) {
                userDB.setEmail(properties.get("email").toString());
                userService.save(userDB);
            }
        }

        // Location
        if (properties.get("location") == null) {
            if (userDB.getCountry() != null) {
                userDB.setCountry(null);
                userService.save(userDB);
            }
        } else {
            if (userDB.getCountry() == null || !userDB.getCountry().equals(properties.get("location").toString())) {
                userDB.setCountry(properties.get("location").toString());
                userService.save(userDB);
            }
        }

        // Bio
        if (properties.get("bio") == null) {
            if (userDB.getBio() != null) {
                userDB.setBio(null);
                userService.save(userDB);
            }
        } else {
            if (userDB.getBio() == null || !userDB.getBio().equals(properties.get("bio").toString())) {
                userDB.setBio(properties.get("bio").toString());
                userService.save(userDB);
            }
        }
    }
}