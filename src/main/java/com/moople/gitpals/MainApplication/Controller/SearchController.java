package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class SearchController
{
    @Autowired
    private userInterface userInteface;

    @Autowired
    private projectInterface projectInteface;

    @GetMapping("/search")
    public String searchPage()
    {
        return "sections/searchForm";
    }

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