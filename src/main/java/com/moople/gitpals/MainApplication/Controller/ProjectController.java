package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                requirements
        );

        User userInDB = userInterface.findByUsername(user.getName());

        userInDB.addProject(userProject);

        userInterface.save(userInDB);
        projectInterface.save(userProject);

        return new ModelAndView("redirect:/dashboard");
    }
}