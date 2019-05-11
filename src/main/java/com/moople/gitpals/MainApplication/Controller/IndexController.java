package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        if(user != null)
        {
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);

            if(userInterface.findByUsername(user.getName()) == null)
            {
                userInterface.save(
                        new User(
                                user.getName(),
                                "https://github.com/" + user.getName(),
                                "",
                                "",
                                technologiesMap,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );
            }

            model.addAttribute("userDB", userInterface.findByUsername(user.getName()));

            User user1 = userInterface.findByUsername(user.getName());

            for(int i = 0; i < user1.getProjects().size(); i++)
            {
                Project project1 = projectInterface.findByTitle(user1.getProjects().get(i));

                if(project1 != null)
                {
                    project1.setAuthorName(user1.getUsername());

                    projectInterface.save(project1);
                }
            }
        }


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

        else
        {
            User userDB = userInterface.findByUsername(user.getName());

            model.addAttribute("dbUser", userDB);
            model.addAttribute("userObject", new User());
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);

            List<Project> appliedToProjects = new ArrayList<>();

            for (int i = 0; i < userDB.getAppliedTo().size(); i++)
            {
                appliedToProjects.add(projectInterface.findByTitle(userDB.getAppliedTo().get(i)));
            }

            model.addAttribute("appliedProjects", appliedToProjects);

            User user1 = userInterface.findByUsername(user.getName());

            for(int i = 0; i < user1.getProjects().size(); i++)
            {
                Project project1 = projectInterface.findByTitle(user1.getProjects().get(i));

                if(project1 != null)
                {
                    project1.setAuthorName(user1.getUsername());

                    projectInterface.save(project1);
                }
            }

            return "sections/dashboard";
        }
    }

    @GetMapping("/userDB")
    public User user(Principal principal)
    {
        return userInterface.findByUsername(principal.getName());
    }

    @GetMapping("/user")
    public Principal userInfo(Principal user)
    {
        return user;
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage(Model model, Principal principal)
    {
        model.addAttribute("usersAmount", userInterface.findAll().size());

        try{
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

    /*
        @param message is taken from html textfield and it's content sent to admin
        @return to index page
     */
    @PostMapping("/reportBug")
    public String bugReported(@RequestParam("bug_description") String message, Principal user)
    {
        User admin = userInterface.findByUsername("danmoop");

        String author = user.getName();

        Message msg = new Message(
                author,
                message
        );

        admin.sendMessage(msg);

        userInterface.save(admin);

        return "redirect:/";
    }
}
