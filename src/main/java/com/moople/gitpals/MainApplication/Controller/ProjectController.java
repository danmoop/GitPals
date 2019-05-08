package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    private userInterface userInterface;

    @Autowired
    private projectInterface projectInterface;

    private final String[] technologies= { "Web design", "Mobile design", "Java", "C++",
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
            {
                techs.put(technology, false);
            }

            model.addAttribute("UserObject", user);
            model.addAttribute("projects", techs);
            model.addAttribute("projectObject", new Project());
            return "sections/projectSubmitForm";
        }
        else
            return "redirect:/";
    }

    @PostMapping("/projectSubmitted")
    public String projectSubmitted(Principal user, @ModelAttribute Project project, @RequestParam("techInput") List<String> techs)
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

    @GetMapping("/projects/{projectname}")
    public String projectPage(@PathVariable String projectname, Model model, Principal user)
    {
        Project project = projectInterface.findByTitle(projectname);

        if(project == null)
        {
            return "error/projectDeleted";
        }

        else
        {
            model.addAttribute("AuthorObject", userInterface.findByUsername(project.getAuthorName()));
            if (user != null) model.addAttribute("userPrincipal", user.getName());
            model.addAttribute("projectObject", project);
            if (user != null) model.addAttribute("userDB", userInterface.findByUsername(user.getName()));
            model.addAttribute("usersApplied", project.getUsersSubmitted());

            return "sections/projectViewPage";
        }
    }

    @PostMapping("/applyForProject")
    public String applyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        User userForApply = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(link);


        if(project.getUsersSubmitted().size() == 0)
        {
            User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

            project.addAppliedUser(userForApply.getUsername());
            userForApply2.addProjectAppliedTo(project.getTitle());

            projectInterface.save(project);
            userInterface.save(userForApply2);
        }

        else
        {
            for(int i = 0; i < project.getUsersSubmitted().size(); i++)
            {
                if(userForApply.getUsername().equals(project.getUsersSubmitted().get(i)))
                {
                    userForApply.deleteProjectAppliedTo(project.getTitle());

                    User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

                    project.deleteAppliedUser(userForApply2.getUsername());

                    Project project2 = projectInterface.findByTitle(project.getTitle());

                    userInterface.save(userForApply2);
                    projectInterface.save(project2);

                    break;
                }
                else
                {
                    userForApply.addProjectAppliedTo(project.getTitle());

                    User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

                    project.addAppliedUser(userForApply2.getUsername());

                    Project project2 = projectInterface.findByTitle(project.getTitle());

                    userInterface.save(userForApply2);
                    projectInterface.save(project2);
                }
            }
        }

        return "redirect:/projects/" + link;
    }

    @PostMapping("/unapplyForProject")
    public String unapplyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        Project projectDB = projectInterface.findByTitle(link);

        User userDB = userInterface.findByUsername(user.getName());

        for(int i = 0; i < projectDB.getUsersSubmitted().size(); i++)
        {
            if(userDB.getUsername().equals(projectDB.getUsersSubmitted().get(i)))
            {
                projectDB.deleteAppliedUser(userDB.getUsername());
                userDB.deleteProjectAppliedTo(projectDB.getTitle());

                projectInterface.save(projectDB);
                userInterface.save(userDB);
            }
        }

        return "redirect:/projects/" + link;
    }

    @PostMapping("/deleteProject")
    public String projectDeleted(Principal user, @RequestParam("projectName") String projectName)
    {
        User userDB = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(projectName);

        if(userDB.getUsername().equals(project.getAuthorName()))
        {
            projectInterface.delete(project);

            for(int q = 0; q < userDB.getProjects().size(); q++)
            {
                if(project.getTitle().equals(userDB.getProjects().get(q)))
                {
                    userDB.deleteProject(userDB.getProjects().get(q));

                    userInterface.save(userDB);
                }
            }

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