package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectService;

    /**
     * @param admin is a current admin authentication
     * @return admin page if username matches and authenticated
     */
    @GetMapping("/admin")
    public String adminPage(Principal admin) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        return "sections/admin";
    }

    /**
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @return list of all users registered
     *
     */
    @PostMapping("/getAllUsers")
    public String getAllUsers(Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        model.addAttribute("users", userService.findAll());

        return "sections/admin";
    }

    /**
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @param username is a user who we want to get information about
     * @return information about this user
     */
    @PostMapping("/getUserInfo")
    public String getUserInfo(@RequestParam("userName") String username, Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user != null) {
            model.addAttribute("user", user.toString());
        } else {
            model.addAttribute("user", username + " is not registered");
        }

        return "sections/admin";
    }

    /**
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @return all the projects added
     */
    @GetMapping("/getAllProjects")
    public String getAllProjects(Principal admin, Model model) {
        if(admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        model.addAttribute("projects", projectService.findAll());

        return "sections/admin";
    }

    /**
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @param projectName is a project we want to get information about
     * @return information about the project
     */
    @PostMapping("/getProjectInfo")
    public String getProjectInfo(@RequestParam("projectName") String projectName, Principal admin, Model model) {
        if(admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        Project project = projectService.findByTitle(projectName);

        if(project == null) {
            model.addAttribute("project", projectName + " is not found");
        } else {
            model.addAttribute("project", project.toString());
        }

        return "sections/admin";
    }
}