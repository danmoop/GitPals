package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProjectInterface projectInterface;

    /**
     * @param projectName is a project name we pass in path
     * @return project json object
     */
    @GetMapping(value = "/get/{project}", produces = "application/json")
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
    @GetMapping(value = "/getAll", produces = "application/json")
    public List<Project> allProjects() {
        return projectInterface.findAll();
    }

    /**
     * @return total number of projects created on GitPals
     */
    @GetMapping(value = "/getNumberOfProjects", produces = "application/json")
    public Map<String, Integer> getNumberOfProjects() {
        Map<String, Integer> map = new HashMap<>();
        map.put("totalNumberOfProjects", projectInterface.findAll().size());

        return map;
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
}