package com.moople.gitpals.MainApplication.Controller.API;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.*;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    @Autowired
    private JWTUtil jwtUtil;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * @param projectName is a project name we pass in path
     * @return project json object
     */
    @GetMapping(value = "/get/{project}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable("project") String projectName) {
        Project project = projectInterface.findByTitle(projectName);

        if (project != null) {
            return project;
        }

        return Data.EMPTY_PROJECT;
    }

    /**
     * @return list of all projects created from the database
     */
    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> allProjects() {
        return projectInterface.findAll();
    }

    /**
     * @return total number of projects created on GitPals
     */
    @GetMapping(value = "/getNumberOfProjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getNumberOfProjects() {
        Map<String, Integer> map = new HashMap<>();
        map.put("numberOfProjects", projectInterface.findAll().size());

        return map;
    }

    /**
     * @param amount is an amount of projects we want to get from the huge list
     * @return list of project which length == amount, so we get fixed list
     */
    @GetMapping(value = "/getAmount/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getSomeProjects(@PathVariable("amount") int amount) {
        return projectInterface.findAll()
                .stream()
                .limit(amount)
                .collect(Collectors.toList());
    }

    /**
     * This function lets user either become applied to a project, or un-applied, if they were applied earlier
     *
     * @param data is an information about user (jwt & project's name, in which they want to apply/unapply)
     * @return a response, which is OK if project and user exist in the database
     */
    @PostMapping(value = "/toggleApplicationToAProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response changeApplicationToAProject(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String projectName = data.get("projectName");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectInterface.findByTitle(projectName);

        if (user == null || project == null) {
            return Response.FAILED;
        }

        if (project.getAppliedUsers().contains(user.getUsername())) {
            project.getAppliedUsers().remove(user.getUsername());
            user.getProjectsAppliedTo().remove(projectName);
        } else {
            project.getAppliedUsers().add(user.getUsername());
            user.getProjectsAppliedTo().add(projectName);

            User projectAuthor = userService.findByUsername(project.getAuthorName());
            Notification notification = new Notification(user.getUsername() + " applied to your project " + project.getTitle());

            projectAuthor.getNotifications().getValue().put(notification.getKey(), notification);
            projectAuthor.getNotifications().setKey(projectAuthor.getNotifications().getKey() + 1);
            userService.save(projectAuthor);
        }

        projectInterface.save(project);
        userService.save(user);

        return Response.OK;
    }

    /**
     * This function submits a user's project
     *
     * @return response if project with user's chosen title doesn't exist
     */
    @PostMapping(value = "/submitProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response submitProject(@RequestBody Map<String, Object> data) {

        String jwt = (String) data.get("jwt");
        Project project = mapper.convertValue(data.get("project"), Project.class);

        if (projectInterface.findByTitle(project.getTitle()) != null) {
            return Response.PROJECT_EXISTS;
        }

        if (jwtUtil.extractUsername(jwt).equals(project.getAuthorName())) {
            projectInterface.save(project);

            return Response.OK;
        }

        return Response.FAILED;
    }

    /**
     * This request is handled when user wants to delete project
     * It will be deleted and applied users will be notified about that
     *
     * @param data is information sent from the user, which contains user's jwt and information about the project
     * @return a response, which is OK if user is the author of the project
     */
    @PostMapping("/deleteProject")
    public Response deleteProject(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String projectName = data.get("projectName");

        User projectAuthor = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectInterface.findByTitle(projectName);

        if (projectAuthor == null || project == null) {
            return Response.FAILED;
        }

        if (projectAuthor.getUsername().equals(project.getAuthorName())) {
            projectInterface.delete(project);

            if (projectAuthor.getSubmittedProjects().contains(project.getTitle())) {
                projectAuthor.getSubmittedProjects().remove(project.getTitle());

                userService.save(projectAuthor);
            }

            // Remove project from everyone who applied to this project
            // First we stream, it returns list of Strings, map them to User object
            List<User> allUsers = project.getAppliedUsers().stream()
                    .map(submittedUser -> userService.findByUsername(submittedUser))
                    .collect(Collectors.toList());

            for (User _user : allUsers) {
                Notification notification = new Notification("A project " + projectName + " you were applied to has been deleted by the project author");
                _user.getProjectsAppliedTo().remove(project.getTitle());
                _user.getNotifications().setKey(_user.getNotifications().getKey() + 1);
                _user.getNotifications().getValue().put(notification.getKey(), notification);

                userService.save(_user);
            }
        }

        return Response.OK;
    }

    /**
     * This request is handled when user submits their comment
     * It will be added to comments list and saved
     *
     * @param data is information sent from the user, which contains user's jwt and information about the project
     * @return a response, which is OK if a comment has been added successfully
     */
    @PostMapping("/sendComment")
    public Response sendComment(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String author = data.get("author");
        String text = data.get("text");
        String projectName = data.get("projectName");

        Project project = projectInterface.findByTitle(projectName);

        if (project == null) {
            return Response.PROJECT_NOT_FOUND;
        }

        if (jwtUtil.extractUsername(jwt).equals(author)) {
            Comment comment = new Comment(author, text);

            // Let the project author know someone has left a comment in a comment section for their project
            User projectAuthor = userService.findByUsername(project.getAuthorName());
            Notification notification = new Notification(author + " has left a comment on your project " + projectName + ": " + comment.getText());

            projectAuthor.getNotifications().getValue().put(notification.getKey(), notification);
            projectAuthor.getNotifications().setKey(projectAuthor.getNotifications().getKey() + 1);
            userService.save(projectAuthor);

            project.getComments().add(comment);
            projectInterface.save(project);

            return Response.OK;
        }

        return Response.FAILED;
    }

    /**
     * This request is handled when user wants to remove their comment
     *
     * @param data is information sent from the user, which contains user's jwt and information about the project
     * @return a response, which is OK if a comment has been removed successfully
     */
    @PostMapping("/removeComment")
    public Response removeComment(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String projectName = (String) data.get("projectName");
        String commentText = (String) data.get("commentText");

        User sender = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectInterface.findByTitle(projectName);

        Optional<Comment> comment = project.getComments()
                .stream()
                .filter(projectComment -> projectComment.getAuthor().equals(sender.getUsername()) && projectComment.getText().equals(commentText))
                .findFirst();

        if (comment.isPresent() && comment.get().getAuthor().equals(sender.getUsername())) {
            project.getComments().remove(comment.get());
            projectInterface.save(project);

            return Response.OK;
        }

        return Response.FAILED;
    }
}