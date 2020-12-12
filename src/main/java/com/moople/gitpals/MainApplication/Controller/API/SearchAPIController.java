package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
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
     * This function returns a user by username
     *
     * @param username is a user's username
     * @return user
     */
    @GetMapping(value = "/getUserByUsername/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return Data.EMPTY_USER;
        }

        // It is not safe to send user's messages/notifications messages to anyone, so remove them
        user.setDialogs(null);
        user.setNotifications(null);

        return user;
    }

    /**
     * This function returns a project by its title
     *
     * @param title is a project title
     * @return project
     */
    @GetMapping(value = "/getProjectByTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Project getProjectByTitle(@PathVariable String title) {
        Project project = projectInterface.findByTitle(title);

        if (project != null) {
            return project;
        }

        return Data.EMPTY_PROJECT;
    }

    /**
     * This function returns a list of users whose username matches the input
     *
     * @param userName is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/matchUsersByUsername/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> foundUsers(@PathVariable String userName) {
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
    public List<Project> foundProjects(@PathVariable String projectName) {
        return projectInterface.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of project with requirements (skills) requested by a user
     *
     * @param technologies is a list of technologies project should contain
     * @return list of projects whose requirements match the user's choice
     */
    @GetMapping(value = "/matchProjectsByTechnologies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getSortedProjects(@RequestBody List<String> technologies) {
        return projectInterface.findAll().stream()
                .filter(project -> technologies.stream()
                        .anyMatch(req -> project.getTechnologies().contains(req.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @return list of posts, whose titles match the target value sent by the user
     */
    @GetMapping(value = "/matchForumPostsByTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> getPostsByTitle(@PathVariable String title) {
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
    public List<ForumPost> getPostsByUser(@PathVariable String author) {
        return forumInterface.findByAuthor(author);
    }
}