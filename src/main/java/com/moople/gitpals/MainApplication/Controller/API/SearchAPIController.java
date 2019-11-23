package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/search")
public class SearchAPIController {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * @param username is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/findUser/{userName}", produces = "application/json")
    public List<User> foundUsers(@PathVariable("userName") String username) {
        return userInterface.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * @param projectName is a project name we pass in path
     * @return list of projects whose title match the one we pass
     */
    @GetMapping(value = "/findProject/{projectName}", produces = "application/json")
    public List<Project> foundProjects(@PathVariable("projectName") String projectName) {
        return projectInterface.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());
    }
}