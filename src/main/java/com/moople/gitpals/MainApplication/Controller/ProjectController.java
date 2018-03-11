package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController
{
    @Autowired
    userInterface userInterface;

    @Autowired
    projectInterface projectInterface;

    @GetMapping("/submitProject")
    public ModelAndView projectForm(Principal user, Model model)
    {

        if(!user.getName().equals("null"))
        {

            String technologies[] = { "Web design", "Mobile design", "Java", "C++",
                    "Python", "Machine learning", "Deep learning", "Ionic",
                    "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
                    "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
                    "C", "GLSL", "OpenGL", "HTML5" };

            Map<String, Boolean> techs = new HashMap<>();

            for(int i = 0; i < technologies.length; i++)
            {
                techs.put(technologies[i], false);
            }

            model.addAttribute("UserObject", user);
            model.addAttribute("projects", techs);
            model.addAttribute("projectObject", new Project());
            return new ModelAndView("sections/projectSubmitForm");
        }
        else
            return new ModelAndView("redirect:/");
    }

    @PostMapping("/projectSubmitted")
    public ModelAndView projectSubmitted(Principal user, @ModelAttribute Project project, @RequestParam("techInput") List<String> techs)
    {
        List<String> requirements = new ArrayList<>();

        for(String tech : techs)
        {
            requirements.add(tech);
        }

        Project userProject = new Project(
                project.getTitle(),
                project.getDescription(),
                project.getGithubProjectLink(),
                userInterface.findByUsername(user.getName()),
                requirements,
                new ArrayList<>()
        );

        User userInDB = userInterface.findByUsername(user.getName());

        userInDB.addProject(userProject);

        userInterface.save(userInDB);
        projectInterface.save(userProject);

        return new ModelAndView("redirect:/dashboard");
    }

    @GetMapping("/projects/{projectname}")
    public ModelAndView projectPage(@PathVariable String projectname, Model model, Principal user)
    {
        Project project = projectInterface.findByTitle(projectname);

        if(project == null)
            return new ModelAndView("redirect:/");
        else
        {
            model.addAttribute("AuthorObject", project.getAuthor());
            model.addAttribute("projectObject", project);
            model.addAttribute("userDB", userInterface.findByUsername(user.getName()));
            model.addAttribute("usersApplied", project.getUsersSubmitted());

            return new ModelAndView("sections/projectViewPage");
        }
    }

    @PostMapping("/applyForProject")
    public ModelAndView applyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        User userForApply = userInterface.findByUsername(user.getName());
        Project project = projectInterface.findByTitle(link);


        if(project.getUsersSubmitted().size() == 0)
        {
            User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

            project.addAppliedUser(userForApply);
            userForApply2.addProjectAppliedTo(project);

            projectInterface.save(project);
            userInterface.save(userForApply2);
        }

        else
        {
            for(int i = 0; i < project.getUsersSubmitted().size(); i++)
            {
                if(userForApply.getUsername().equals(project.getUsersSubmitted().get(i).getUsername()))
                {
                    userForApply.deleteProjectAppliedTo(project);

                    User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

                    project.deleteAppliedUser(userForApply2);

                    Project project2 = projectInterface.findByTitle(project.getTitle());

                    userInterface.save(userForApply2);
                    projectInterface.save(project2);

                    break;
                }
                else
                {
                    userForApply.addProjectAppliedTo(project);

                    User userForApply2 = userInterface.findByUsername(userForApply.getUsername());

                    project.addAppliedUser(userForApply2);

                    Project project2 = projectInterface.findByTitle(project.getTitle());

                    userInterface.save(userForApply2);
                    projectInterface.save(project2);
                }
            }
        }

        return new ModelAndView("redirect:/projects/" + link);
    }

    @PostMapping("/unapplyForProject")
    public ModelAndView unapplyForProject(@RequestParam("linkInput") String link, Principal user)
    {
        Project projectDB = projectInterface.findByTitle(link);
        User userDB = userInterface.findByUsername(user.getName());

        for(int i = 0; i < projectDB.getUsersSubmitted().size(); i++)
        {
            if(userDB.getUsername().equals(projectDB.getUsersSubmitted().get(i).getUsername()))
            {
                User thatUser = projectDB.getUsersSubmitted().get(i);

                projectDB.deleteAppliedUser(thatUser);
                thatUser.deleteProjectAppliedTo(projectDB);


                projectInterface.save(projectDB);
                userInterface.save(thatUser);

                break;
            }
        }

        return new ModelAndView("redirect:/projects/" + link);
    }
}