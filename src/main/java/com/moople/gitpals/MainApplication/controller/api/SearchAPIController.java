package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.model.ForumPost;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.ForumService;
import com.moople.gitpals.MainApplication.service.ProjectService;
import com.moople.gitpals.MainApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/search")
public class SearchAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    /**
     * This function returns a list of users whose username matches the input
     *
     * @param username is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @GetMapping(value = "/matchUsersByUsername/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> matchUsersByUsername(@PathVariable String username) {
        return userService.matchUsersByUsername(username);
    }

    /**
     * This function returns a list of project which title matches the input
     *
     * @param title is a project name we pass in path
     * @return list of projects whose title match the one we pass
     */
    @GetMapping(value = "/matchProjectsByProjectName/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByProjectName(@PathVariable String title) {
        return projectService.matchProjectsByProjectTitle(title);
    }

    /**
     * This function returns a list of projects with technologies specified by a user
     *
     * @param technologies is a list of technologies project should contain
     * @return list of projects whose technologies match the user's input
     */
    @GetMapping(value = "/matchProjectsByTechnologies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByTechnologies(@RequestBody List<String> technologies) {
        return projectService.matchProjectsByTechnologies(technologies);
    }

    /**
     * This function returns a list of projects with roles specified by a user
     *
     * @param roles is a list of required roles project should contain
     * @return list of projects whose required roles match the user's input
     */
    @GetMapping(value = "/matchProjectsByRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> matchProjectsByRoles(@RequestBody List<String> roles) {
        return projectService.matchProjectsByRoles(roles);
    }

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @return list of posts, whose titles match the target value sent by the user
     */
    @GetMapping(value = "/matchForumPostsByTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> matchForumPostsByTitle(@PathVariable String title) {
        return forumService.matchForumPostsByTitle(title);
    }

    /**
     * This function returns all the posts from a particular user
     *
     * @return all the posts from the user specified
     */
    @GetMapping(value = "/matchForumPostsByAuthor/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> matchForumPostsByAuthor(@PathVariable String author) {
        return forumService.findByAuthor(author);
    }
}