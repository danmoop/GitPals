package com.moople.gitpals.MainApplication.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moople.gitpals.MainApplication.configuration.JWTUtil;
import com.moople.gitpals.MainApplication.model.Comment;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.Response;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.ProjectService;
import com.moople.gitpals.MainApplication.service.UserService;
import com.moople.gitpals.MainApplication.tools.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JWTUtil jwtUtil;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * This function returns an object fetched from the database by its title
     *
     * @param title is a project title we pass in path
     * @return project json object
     */
    @GetMapping(value = "/getByTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable String title) {
        Project project = projectService.findByTitle(title);

        if (project != null) {
            return project;
        }

        return Data.EMPTY_PROJECT;
    }

    /**
     * This function returns an object fetched from the database by its unique id
     *
     * @param id is project's unique id number, which we use to find it in the database
     * @return project json object or empty project if such id is not found
     */
    @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Project getProjectById(@PathVariable String id) {
        return projectService.findById(id);
    }

    /**
     * @return list of all projects created from the database
     */
    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> allProjects() {
        return projectService.findAll();
    }

    /**
     * @return total number of projects created on GitPals
     */
    @GetMapping(value = "/getNumberOfProjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getNumberOfProjects() {
        Map<String, Integer> map = new HashMap<>();
        map.put("numberOfProjects", projectService.findAll().size());

        return map;
    }

    /**
     * @param amount is an amount of projects we want to get from the huge list
     * @return list of project which length == amount, so we get fixed list
     */
    @GetMapping(value = "/getAmount/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getSomeProjects(@PathVariable("amount") int amount) {
        return projectService.getFixedNumberOfProjects(amount);
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
        Project project = projectService.findByTitle(projectName);

        if (user == null || project == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        projectService.changeApplicationToAProject(project, user);

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

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = mapper.convertValue(data.get("project"), Project.class);

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        if (projectService.findByTitle(project.getTitle()) != null) {
            return Response.PROJECT_EXISTS;
        }

        if (jwtUtil.extractUsername(jwt).equals(project.getAuthorName())) {
            Project dummy = new Project(); // This is used to obtain ObjectId, which is set to the one user submits
            project.setId(dummy.getId());

            user.getSubmittedProjects().add(project.getTitle());

            userService.save(user);
            projectService.save(project);

            return Response.OK;
        }

        return Response.FAILED;
    }

    /**
     * This function edits information about the project
     *
     * @param data is data sent from the user, which contain new information about the project
     * @return response if the user is the project's author and information has been changed
     */
    @PostMapping(value = "/editProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response editProject(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        Project project = mapper.convertValue(data.get("project"), Project.class);
        Project projectDB = projectService.findById(project.getId());

        if (projectDB.equals(Data.EMPTY_PROJECT)) {
            return Response.FAILED;
        }

        if (projectDB.getAuthorName().equals(user.getUsername())) {
            projectService.editProjectInfo(
                    projectDB,
                    project.getTitle(),
                    project.getDescription(),
                    project.getGithubProjectLink(),
                    project.getTechnologies(),
                    project.getRequiredRoles()
            );

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
    @PostMapping(value = "/deleteProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteProject(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String projectName = data.get("projectName");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectService.findByTitle(projectName);

        if (user == null || project == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        if (user.getUsername().equals(project.getAuthorName())) {
            projectService.deleteProject(project, user);

            return Response.OK;
        }

        return Response.FAILED;
    }

    /**
     * This request is handled when user submits their comment
     * It will be added to comments list and saved
     *
     * @param data is information sent from the user, which contains user's jwt and information about the project
     * @return a response, which is OK if a comment has been added successfully
     */
    @PostMapping(value = "/sendComment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment sendComment(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String author = data.get("author");
        String text = data.get("text");
        String projectName = data.get("projectName");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectService.findByTitle(projectName);

        if (project == null || user == null || user.isBanned()) {
            return Data.EMPTY_COMMENT;
        }
        if (jwtUtil.extractUsername(jwt).equals(author)) {
            Comment comment = new Comment(author, text);

            projectService.sendComment(project, comment, user);

            return comment;
        }
        return Data.EMPTY_COMMENT;
    }

    /**
     * This function edits a comment in a project (changes comment's context & marks it as edited)
     *
     * @param data is information sent by the user, which contains information about the comment & project
     *             This information helps to easily find the comment by its key and modify it
     * @return project page with edited comment contents
     */
    @PostMapping(value = "/editProjectComment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response editProjectComment(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String projectName = data.get("projectName");
        String text = data.get("text");
        String commentKey = data.get("commentKey");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectService.findByTitle(projectName);

        if (user == null || project == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        projectService.editComment(project, text, commentKey, user.getUsername());

        return Response.OK;
    }

    /**
     * This request is handled when user wants to remove their comment
     *
     * @param data is information sent from the user, which contains user's jwt and information about the project
     * @return a response, which is OK if a comment has been removed successfully
     */
    @PostMapping(value = "/removeComment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response removeComment(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String projectName = (String) data.get("projectName");
        String commentText = (String) data.get("commentText");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        Project project = projectService.findByTitle(projectName);

        if (user == null || project == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        if (projectService.removeComment(project, user.getUsername(), commentText)) {
            return Response.OK;
        }

        return Response.FAILED;
    }
}