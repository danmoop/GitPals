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
public class SearchController
{
    @Autowired
    private UserInterface userInteface;

    @Autowired
    private ProjectInterface projectInteface;

    @GetMapping("/search")
    public String searchPage()
    {
        return "sections/searchForm";
    }

    /*
        @param username is taken from a html textfield
        @return list of users whose nicknames contain user's input
     */
    @PostMapping("/findUser")
    public String foundUsers(@RequestParam("user_name") String username, Model model)
    {
        List<User> allUsers = userInteface.findAll();

        List<String> matchUsers = allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .map(User::getUsername).collect(Collectors.toList());

        model.addAttribute("match_users", matchUsers);

        return "sections/matchUsers";
    }

    /*
        @param projectName is taken from a html textfield
        @return list of projects whose titles contain user's input
     */
    @PostMapping("/findProject")
    public String foundProjects(@RequestParam("project_name") String projectName, Model model)
    {
        List<Project> allProjects = projectInteface.findAll();

        List<Project> matchProjects = allProjects.stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("match_projects", matchProjects);

        return "sections/matchProjects";
    }
}