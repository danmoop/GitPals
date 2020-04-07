package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/search")
public class SearchAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * This function returns a user by username
     *
     * @param username is a user's username
     * @return user
     */
    @GetMapping(value = "/findByUsername/{username}", produces = "application/json")
    public User getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    /**
     * This function returns a project by its title
     *
     * @param title is a project title
     * @return project
     */
    @GetMapping(value = "/findByTitle/{title}", produces = "application/json")
    public Project getProjectByTitle(@PathVariable String title) {
        return projectInterface.findByTitle(title);
    }

    /**
     * This function returns a list of users whose username matches the input
     *
     * @param username is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/matchByUsername/{userName}", produces = "application/json")
    public List<User> foundUsers(@PathVariable String username) {
        return userService.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of project which title matches the input
     *
     * @param projectName is a project name we pass in path
     * @return list of projects whose title match the one we pass
     */
    @GetMapping(value = "/matchByProjectName/{projectName}", produces = "application/json")
    public List<Project> foundProjects(@PathVariable String projectName) {
        return projectInterface.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of project with requirements requested by a user
     *
     * @param requirements is a list of requirements project should contain
     * @return list of projects whose requirements match the user's choice
     */
    @GetMapping(value = "/matchProjectsByRequirements", produces = "application/json")
    public List<Project> getSortedProjects(@RequestBody List<String> requirements) {
        return projectInterface.findAll().stream()
                .filter(project -> requirements.stream()
                        .anyMatch(req -> project.getRequirements().contains(req)))
                .collect(Collectors.toList());
    }
}