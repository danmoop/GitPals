package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class IndexController
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

    @GetMapping("/")
    public String indexPage(Principal user, Model model)
    {

        Map<String, Boolean> technologiesMap = new HashMap<>();

        for (String technology : technologies) {
            technologiesMap.put(technology, false);
        }

        // If we are logged in, display information about us on the index page
        if(user != null)
        {
            model.addAttribute("GithubUser", user);

            // If we are logged in but there is no our user object in database, save it
            // Usually this function is executed once when we are register for the first time
            if(userInterface.findByUsername(user.getName()) == null)
            {
                userInterface.save(

                        /*
                        *  There are some arguments in the constructor that were added
                        *  due to @AllArgsConstructor annotation in User model, so
                        *  I have to write them all here
                        */

                        new User(
                                null,
                                user.getName(),
                                "https://github.com/" + user.getName(),
                                "", // country
                                "", // user info
                                technologiesMap,
                                new ArrayList<>(), // created projects list
                                new ArrayList<>(), // applied projects list
                                new ArrayList<>() // messages list
                        )
                );
            }

            model.addAttribute("userDB", userInterface.findByUsername(user.getName()));
        }

        // Show the most recent projects (only 50)
        List<Project> projects = projectInterface.findAll().stream()
                .limit(50).collect(Collectors.toList());

        model.addAttribute("projectTechs", technologiesMap);
        model.addAttribute("projects", projects);
        model.addAttribute("totalProjectsAmount", projectInterface.findAll().size());

        return "sections/index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Principal user, Model model)
    {
        // if user is not logged in - redirect to index
        if(user == null)
            return "redirect:/";

        // user is logged in
        else
        {
            User userDB = userInterface.findByUsername(user.getName());

            model.addAttribute("dbUser", userDB);
            model.addAttribute("userObject", new User());
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);

            List<Project> appliedToProjects = new ArrayList<>();

            for (int i = 0; i < userDB.getProjectsAppliedTo().size(); i++)
            {
                appliedToProjects.add(projectInterface.findByTitle(userDB.getProjectsAppliedTo().get(i)));
            }

            model.addAttribute("appliedProjects", appliedToProjects);

            return "sections/dashboard";
        }
    }

    @GetMapping("/userDB")
    public User user(Principal principal)
    {
        return userInterface.findByUsername(principal.getName());
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession)
    {
        httpSession.invalidate();
        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage(Model model, Principal principal)
    {
        model.addAttribute("usersAmount", userInterface.findAll().size());

        try {
            model.addAttribute("LoggedUser", principal.getName());
        } catch (NullPointerException e) {
            model.addAttribute("LoggedUser", null);
        }

        return "sections/aboutPage";
    }

    @GetMapping("/bugReport")
    public String bugReport()
    {
        return "sections/bugReport";
    }

    @GetMapping("/donate")
    public String donatePage()
    {
        return "sections/donate";
    }
}
