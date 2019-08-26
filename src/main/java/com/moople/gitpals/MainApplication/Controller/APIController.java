package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class APIController {
    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * @return users' principal github object (json), containing information about them (github username, avatar image etc.)
     */
    @GetMapping(value = "/api/principal", produces = "application/json")
    public Principal getPrincipal(Principal user) {
        if (user != null)
            return user;

        return () -> "Unauthorized";
    }

    /**
     * @return users' gitpals user object(json), containing information about them (projects, message, etc)
     */
    @GetMapping(value = "/api/user", produces = "application/json")
    public User getUser(Principal user) {
        if (user != null)
            return userInterface.findByUsername(user.getName());

        return new User();
    }

    /**
     * @return project json object
     */
    @GetMapping(value = "api/project/{project}", produces = "application/json")
    public Project getProject(Principal principal, @PathVariable("project") String projectName) {
        Project project = projectInterface.findByTitle(projectName);

        if (project != null && principal != null && principal.getName().equals(project.getAuthorName()))
            return project;

        return new Project();
    }
}