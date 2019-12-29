package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectAPIController {

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * @param projectName is a project name we pass in path
     * @return project json object
     */
    @GetMapping(value = "/get/{project}", produces = "application/json")
    public Project getProject(@PathVariable("project") String projectName) {
        Project project = projectInterface.findByTitle(projectName);

        if (project != null)
            return project;

        return new Project();
    }

    /**
     * @return list of all projects created from the database
     */
    @GetMapping(value = "/getAll", produces = "application/json")
    public List<Project> allProjects() {
        return projectInterface.findAll();
    }

    /**
     * @param amount is an amount of projects we want to get from the huge list
     * @return list of project which length == amount, so we get fixed list
     */
    @GetMapping(value = "/getAmount/{amount}", produces = "application/json")
    public List<Project> getSomeProjects(@PathVariable("amount") int amount) {
        return projectInterface.findAll()
                .stream()
                .limit(amount)
                .collect(Collectors.toList());
    }

    /**
     * Project is created and added to database
     *
     * @param map is a hashmap that comes from the client, data is being extracted and assigned
     * @return response according to the success of a function
     */
    @PostMapping(value = "/createProject")
    public Response submitProject(@RequestBody Map<Object, Object> map) {
        Project project = new Project(
                (String) map.get("title"),
                (String) map.get("description"),
                (String) map.get("githubProjectLink"),
                (String) map.get("username"),
                (List<String>) map.get("requirements"));

        if (projectInterface.findByTitle(project.getTitle()) == null) {
            projectInterface.save(project);

            return Response.OK;
        } else {
            return Response.PROJECT_EXISTS;
        }
    }

    /**
     * Project deleted from the database
     *
     * @param map is a hashmap that comes from the client, data is being extracted and assigned
     * @return response according to the success of a function
     */
    @PostMapping("/deleteProject")
    public Response deleteProject(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String projectName = map.get("projectName");

        Project project = projectInterface.findByTitle(projectName);

        if (project != null && project.getAuthorName().equals(username)) {
            projectInterface.delete(projectInterface.findByTitle(project.getTitle()));
            return Response.OK;
        }

        return Response.FAILED;
    }

    // TEST AREA ///

    @GetMapping("/create/{amount}")
    public Response createProjects(@PathVariable int amount) {

        for (int i = 0; i < amount; i++) {
            String s = String.valueOf(i);
            Project project = new Project(getRandomKey(), s, s, s, new ArrayList<>());

            projectInterface.save(project);
        }

        return Response.OK;
    }

    @GetMapping("/killThemAll")
    public Response deleteProjects() {
        projectInterface.deleteAll();

        return Response.OK;
    }

    private String getRandomKey() {
        StringBuilder b = new StringBuilder();
        Random rand = new Random();

        String possible = "qwertyuiopasdfghjklzxcvbnm";

        for (int i = 0; i < 10; i++) {
            b.append(possible.charAt(rand.nextInt(possible.length())));
        }

        return b.toString();
    }
}