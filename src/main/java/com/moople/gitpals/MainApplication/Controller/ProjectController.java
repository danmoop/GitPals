package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController
{
    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    private final String[] technologies = { "Web design", "Mobile design", "Java", "C++",
            "Python", "Machine learning", "Deep learning", "Ionic",
            "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
            "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
            "C", "GLSL", "OpenGL", "HTML5", "C#", "Swift", "Big Data", "CSS",
            "Game modding", "Other"
    };

    @GetMapping("/submitProject")
    public String projectForm(Principal user, Model model)
    {
        if(!user.getName().equals("null"))
        {
            Map<String, Boolean> techs = new HashMap<>();

            for (String technology : technologies)
                techs.put(technology, false);

            model.addAttribute("UserObject", user);
            model.addAttribute("projects", techs);
            model.addAttribute("projectObject", new Project());

            return "sections/projectSubmitForm";
        }
        else
            return "redirect:/";
    }

    /**
        @param project is taken from html form with all the data (project name, description etc.)
        @param techs is a checkbox list of technologies for a project that user selects
        @return create project and redirect to its page, otherwise show an error messaging about identical project name
     **/
    @PostMapping("/projectSubmitted")
    public String projectSubmitted(
            Principal user,
            @ModelAttribute Project project,
            @RequestParam("techInput") List<String> techs)
    {
        Project project1 = projectInterface.findByTitle(project.getTitle());

        if(project1 == null)
        {
            List<String> requirements = new ArrayList<>(techs);

            Project userProject = new Project(
                    project.getTitle(),
                    project.getDescription(),
                    project.getGithubProjectLink(),
                    userInterface.findByUsername(user.getName()).getUsername(),
                    requirements,
                    new ArrayList<>()
            );

            User userInDB = userInterface.findByUsername(user.getName());

            userInDB.addProject(userProject.getTitle());

            userInterface.save(userInDB);
            projectInterface.save(userProject);

            return "redirect:/projects/" + userProject.getTitle();
        }

        else
        {
            return "error/projectExists";
        }
    }

    /**
        @param projectName is taken from an address field - like "/project/UnrealEngine"
        @return project page with it's title, author, description, technologies etc
     **/
    @GetMapping("/projects/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model, Principal user)
    {
        Project project = projectInterface.findByTitle(projectName);

        if(project == null)
        {
            return "error/projectDeleted";
        }

        else
        {
            model.addAttribute("AuthorObject", userInterface.findByUsername(project.getAuthorName()));
            model.addAttribute("projectObject", project);
            model.addAttribute("usersApplied", project.getUsersSubmitted());

            if (user != null)
            {
                model.addAttribute("userDB", userInterface.findByUsername(user.getName()));
                model.addAttribute("userPrincipal", user.getName());
            }

            return "sections/projectViewPage";
        }
    }

    /**
        @param link is project's title which is taken from a hidden html textfield (value assigned automatically with thymeleaf)
        @return redirect to the same project page
     **/
    @PostMapping("/applyForProject")
    public String applyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        User userForApply = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(link);

        if(!project.getUsersSubmitted().contains(user.getName()))
        {
            project.addAppliedUser(userForApply.getUsername());
            userForApply.addProjectAppliedTo(project.getTitle());

            projectInterface.save(project);
            userInterface.save(userForApply);
        }

        return "redirect:/projects/" + link;
    }

    /**
        @param link is project's title which is taken from a hidden html textfield (value assigned automatically with thymeleaf)
        @return redirect to the same project page
     **/
    @PostMapping("/unapplyForProject")
    public String unapplyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        Project projectDB = projectInterface.findByTitle(link);

        User userDB = userInterface.findByUsername(user.getName());

        if (projectDB.getUsersSubmitted().contains(userDB.getUsername()))
        {
            projectDB.deleteAppliedUser(userDB.getUsername());
            userDB.deleteProjectAppliedTo(projectDB.getTitle());

            projectInterface.save(projectDB);
            userInterface.save(userDB);
        }

        return "redirect:/projects/" + link;
    }

    /**
        @param projectName is project's title which is taken from a html textfield
        @return redirect to the index page
     **/
    @PostMapping("/deleteProject")
    public String projectDeleted(Principal user, @RequestParam("projectName") String projectName)
    {
        User userDB = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(projectName);

        // Remove project from author's projects list
        if(userDB.getUsername().equals(project.getAuthorName()))
        {
            projectInterface.delete(project);

            if (userDB.getProjects().contains(project.getTitle()))
            {
                userDB.deleteProject(project.getTitle());

                userInterface.save(userDB);
            }

            // Remove project from everyone who applied to this project
            List<User> allUsers = userInterface.findAll();

            for (User allUser : allUsers)
            {
                allUser.deleteProjectAppliedTo(project.getTitle());

                userInterface.save(allUser);
            }

            return "redirect:/";

        }

        else
        {
            return "error/siteBroken";
        }
    }

    /**
        @param data is a list of technologies checkboxes user select manually
        @return a list of projects according to user's preference
     **/
    @PostMapping("/sortProjects")
    public String projectsSorted(@RequestParam("sort_projects") List <String> data, Model model)
    {
        List<Project> allProjects = projectInterface.findAll();

        List<Project> matchProjects = allProjects.stream()
                .filter(project -> data.stream()
                    .anyMatch(req -> project.getRequirements().contains(req))).collect(Collectors.toList());

        model.addAttribute("matchProjects", matchProjects);

        return "sections/projectsAfterSorting";
    }
}