package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        return new Project();
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
        map.put("totalNumberOfProjects", projectInterface.findAll().size());

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
    @PostMapping(value = "/changeApplicationToAProject", produces = MediaType.APPLICATION_JSON_VALUE)
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
        }

        projectInterface.save(project);
        userService.save(user);

        return Response.OK;
    }

    /**
     * This function submits a user's project
     *
     * @param project is a project, which is sent from the user's phone
     * @return response if project with user's chosen title doesn't exist
     */
    @PostMapping(value = "/submitProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response submitProject(@RequestBody Project project) {
        if (projectInterface.findByTitle(project.getTitle()) == null) {
            projectInterface.save(project);
            return Response.OK;
        }

        return Response.PROJECT_EXISTS;
    }
}