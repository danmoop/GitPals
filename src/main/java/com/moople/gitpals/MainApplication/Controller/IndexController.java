package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

@Controller
public class IndexController
{

    @Autowired
    userInterface userInterface;

    @Autowired
    projectInterface projectInterface;

    @GetMapping("/")
    public String indexPage(Principal user, Model model)
    {
        String technologies[] = { "Web design", "Mobile design", "Java", "C++",
                "Python", "Machine learning", "Deep learning", "Ionic",
                "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
                "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
                "C", "GLSL", "OpenGL", "HTML5", "C#", "Swift", "Big Data", "CSS",
                "Game modding", "Other"
        };

        Map<String, Boolean> langs = new HashMap<>();

        for (String technology : technologies) {
            langs.put(technology, false);
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
                                "Not set",
                                "Not set",
                                langs,
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

        model.addAttribute("projectTechs", langs);
        model.addAttribute("allTheProjects", projectInterface.findAll());

        return "sections/index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Principal user, Model model)
    {
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

    @Configuration
    @EnableOAuth2Sso
    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/login").permitAll().and()
                    .authorizeRequests();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage(Model model)
    {
        model.addAttribute("usersAmount", userInterface.findAll().size());
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
