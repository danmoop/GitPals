package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {
    @Autowired
    private UserInterface userInteface;

    @Autowired
    private ProjectInterface projectInteface;

    /**
     * This request is handled when user wants to see a search page to find a project or a user
     *
     * @return html page where users can find a project or a user by name
     */
    @GetMapping("/search")
    public String searchPage() {
        return "sections/searchForm";
    }


    /**
     * This request is handled when user submits a username they want to find
     * List of users will be displayed
     *
     * @param username is taken from a html textfield
     * @return list of users whose nicknames contain user's input
     **/
    @PostMapping("/findUser")
    public String foundUsers(@RequestParam("user_name") String username, Model model) {
        List<String> matchUsers = userInteface.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .map(User::getUsername).collect(Collectors.toList());

        model.addAttribute("match_users", matchUsers);

        return "sections/matchUsers";
    }


    /**
     * This request is handled when user submits a project name they want to find
     * A list of projects will be displayed
     *
     * @param projectName is taken from a html textfield
     * @return list of projects whose titles contain user's input
     **/
    @PostMapping("/findProject")
    public String foundProjects(@RequestParam("project_name") String projectName, Model model) {
        List<Project> matchProjects = projectInteface.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("match_projects", matchProjects);

        return "sections/matchProjects";
    }
}