package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @GetMapping(value = "/findByUsername/{username}", produces = "application/json")
    public User getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);

        if (user == null) return null;

        // It is not safe to send user's messages to anyone, so remove them
        user.setMessages(null);

        return user;
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
     * @param userName is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/matchByUsername/{userName}", produces = "application/json")
    public List<User> foundUsers(@PathVariable String userName) {
        return userService.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(userName.toLowerCase()))
                .peek(user -> user.setMessages(null))
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
     * This function returns a list of project with requirements (skills) requested by a user
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

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @return list of posts, whose titles match the target value sent by the user
     */
    @GetMapping(value = "/matchForumPostsByTitle/{title}", produces = "application/json")
    public List<ForumPost> getPostsByTitle(@PathVariable String title) {
        try {
            return forumInterface.findAll().stream()
                    .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * This function returns one particular forum post, which is found by its id
     *
     * @return a forum post with an id prompted by the user
     */
    @GetMapping(value = "/findForumPostById/{id}", produces = "application/json")
    public ForumPost getPostById(@PathVariable String id) {
        try {
            return forumInterface.findByKey(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This function returns all the posts from a particular user
     *
     * @return all the posts who the user
     */
    @GetMapping(value = "/matchForumPostsByAuthor/{author}", produces = "application/json")
    public List<ForumPost> getPostsByUser(@PathVariable String author) {
        try {
            return forumInterface.findByAuthor(author);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}