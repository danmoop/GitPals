package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectController
{
    @Autowired
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * @return html page where users can submit their project
     */
    @GetMapping("/submitProject")
    public String projectForm(Principal user, Model model)
    {
        if (user != null)
        {
            model.addAttribute("UserObject", user);
            model.addAttribute("techs", Data.technologiesMap);
            model.addAttribute("projectObject", new Project());

            return "sections/projectSubmitForm";
        }

        return "redirect:/";
    }

    /**
     * @param project is taken from html form with all the data (project name, description etc.)
     * @param techs   is a checkbox list of technologies for a project that user selects
     * @return create project and redirect to its page, otherwise show an error messaging about identical project name
     **/
    @PostMapping("/projectSubmitted")
    public String projectSubmitted(
            Principal user,
            @ModelAttribute Project project,
            @RequestParam("techInput") List<String> techs)
    {
        Project projectDB = projectInterface.findByTitle(project.getTitle());

        if (projectDB == null)
        {
            Project userProject = new Project(
                    project.getTitle(),
                    project.getDescription(),
                    project.getGithubProjectLink(),
                    userInterface.findByUsername(user.getName()).getUsername(),
                    techs
            );

            User userInDB = userInterface.findByUsername(user.getName());

            userInDB.getProjects().add(userProject.getTitle());

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
     * @param projectName is taken from an address field - like "/project/UnrealEngine"
     * @return html project page with it's title, author, description, technologies etc
     **/
    @GetMapping("/projects/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model, Principal user)
    {
        Project project = projectInterface.findByTitle(projectName);

        if (project == null)
        {
            return "error/projectDeleted";
        }
        else
        {
            model.addAttribute("AuthorObject", userInterface.findByUsername(project.getAuthorName()));
            model.addAttribute("projectObject", project);

            if (user != null)
            {
                model.addAttribute("userDB", userInterface.findByUsername(user.getName()));
            }

            return "sections/projectViewPage";
        }
    }

    /**
     * @param link is project's title which is taken from a hidden html textfield (value assigned automatically with thymeleaf)
     * @return redirect to the same project page
     **/
    @PostMapping("/applyForProject")
    public String applyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        User userForApply = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(link);

        // Users that already submitted can't submit another time, only once per project
        if (!project.getUsersSubmitted().contains(user.getName()))
        {
            project.getUsersSubmitted().add(userForApply.getUsername());
            userForApply.getProjectsAppliedTo().add(project.getTitle());

            projectInterface.save(project);
            userInterface.save(userForApply);
        }

        return "redirect:/projects/" + link;
    }

    /**
     * @param link is project's title which is taken from a hidden html textfield (value assigned automatically with thymeleaf)
     * @return redirect to the same project page
     **/
    @PostMapping("/unapplyForProject")
    public String unapplyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        Project projectDB = projectInterface.findByTitle(link);

        User userDB = userInterface.findByUsername(user.getName());

        if (projectDB.getUsersSubmitted().contains(userDB.getUsername()))
        {
            projectDB.getUsersSubmitted().remove(userDB.getUsername());
            userDB.getProjectsAppliedTo().remove(projectDB.getTitle());

            projectInterface.save(projectDB);
            userInterface.save(userDB);
        }

        return "redirect:/projects/" + link;
    }

    /**
     * @param projectName is project's title which is taken from a html textfield
     * @return redirect to the index page
     **/
    @PostMapping("/deleteProject")
    public String projectDeleted(Principal user, @RequestParam("projectName") String projectName)
    {
        User userDB = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(projectName);

        // Remove project from author's projects list
        if (userDB.getUsername().equals(project.getAuthorName()))
        {
            projectInterface.delete(project);

            if (userDB.getProjects().contains(project.getTitle()))
            {
                userDB.getProjects().remove(project.getTitle());

                userInterface.save(userDB);
            }

            // Remove project from everyone who applied to this project
            // First we stream, it returns list of Strings, map them to User object
            List<User> allUsers = project.getUsersSubmitted().stream()
                    .map(submittedUser -> userInterface.findByUsername(submittedUser))
                    .collect(Collectors.toList());

            for (User allUser : allUsers)
            {
                allUser.getProjectsAppliedTo().remove(project.getTitle());

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
     * @param data     is a list of technologies checkboxes user select manually
     * @param isUnique is a condition whether there are any other techs EXCEPT what users choose (null if checkbox is not selected, "off" if selected)
     * @return a list of projects according to user's preference
     **/
    @PostMapping("/sortProjects")
    public String projectsSorted(@RequestParam("sort_projects") List<String> data, @RequestParam(required = false, name = "isUnique") boolean isUnique, Model model)
    {
        List<Project> allProjects = projectInterface.findAll();

        List<Project> matchProjects;

        /** @param isUnique does the following:
         * if there is a project with some requirements and we mark a checkbox then
         * it will find a project with chosen requirements ONLY
         *
         * if checkbox is not selected it will find the same project by ONE of the requirements
         */
        if (isUnique) // true - if checkbox IS selected
        {
            matchProjects = allProjects.stream()
                    .filter(project -> project.getRequirements().equals(data))
                    .collect(Collectors.toList());
        }
        else // false - checkbox IS NOT selected
        {
            matchProjects = allProjects.stream()
                    .filter(project -> data.stream()
                            .anyMatch(req -> project.getRequirements().contains(req))).collect(Collectors.toList());
        }

        model.addAttribute("matchProjects", matchProjects);

        return "sections/projectsAfterSorting";
    }
}