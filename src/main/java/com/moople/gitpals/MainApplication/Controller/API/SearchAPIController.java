package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This function returns a list of users whose username matches the input
     *
     * @param userName is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/matchUsersByUsername/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> matchUsersByUsername(@PathVariable String userName) {
        return userService.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(userName.toLowerCase()))
                .peek(user -> {
                    user.setDialogs(null);
                    user.setNotifications(null);
                })
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of project which title matches the input
     *
     * @param projectName is a project name we pass in path
     * @return list of projects whose title match the one we pass
     */
    @GetMapping(value = "/matchProjectsByProjectName/{projectName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByProjectName(@PathVariable String projectName) {
        return projectInterface.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of projects with technologies specified by a user
     *
     * @param technologies is a list of technologies project should contain
     * @return list of projects whose technologies match the user's input
     */
    @GetMapping(value = "/matchProjectsByTechnologies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByTechnologies(@RequestBody List<String> technologies) {
        return projectInterface.findAll().stream()
                .filter(project -> technologies
                        .stream()
                        .anyMatch(tech -> project.getTechnologies().contains(tech.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of projects with roles specified by a user
     *
     * @param roles is a list of required roles project should contain
     * @return list of projects whose required roles match the user's input
     */
    @GetMapping(value = "/matchProjectsByRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByRoles(@RequestBody List<String> roles) {
        return projectInterface.findAll()
                .stream()
                .filter(project -> roles.stream()
                        .anyMatch(role -> project.getRequiredRoles().contains(role.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @return list of posts, whose titles match the target value sent by the user
     */
    @GetMapping(value = "/matchForumPostsByTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> matchForumPostsByTitle(@PathVariable String title) {
        return forumInterface.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns all the posts from a particular user
     *
     * @return all the posts who the user
     */
    @GetMapping(value = "/matchForumPostsByAuthor/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> matchForumPostsByAuthor(@PathVariable String author) {
        return forumInterface.findByAuthor(author);
    }
}