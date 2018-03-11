package com.moople.gitpals.MainApplication.Controller;

import com.mongodb.util.JSON;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController
{

    @Autowired
    userInterface userInterface;

    @Autowired
    projectInterface projectInterface;

    @GetMapping("/")
    public ModelAndView indexPage(Principal user, Model model)
    {
        if(user != null)
        {
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);
            model.addAttribute("userDB", userInterface.findByUsername(user.getName()));

            if(userInterface.findByUsername(user.getName()) == null)
            {
                String technologies[] = { "Web design", "Mobile design", "Java", "C++",
                        "Python", "Machine learning", "Deep learning", "Ionic",
                        "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
                        "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
                        "C", "GLSL", "OpenGL", "HTML5" };

                Map<String, Boolean> langs = new HashMap<>();

                for(int i = 0; i < technologies.length; i++)
                {
                    langs.put(technologies[i], false);
                }

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

            User user1 = userInterface.findByUsername(user.getName());

            for(int i = 0; i < user1.getProjects().size(); i++)
            {
                Project project1 = projectInterface.findByTitle(user1.getProjects().get(i).getTitle());

                if(project1 != null)
                {
                    project1.setAuthor(user1);

                    projectInterface.save(project1);
                }
            }
        }

        model.addAttribute("allTheProjects", projectInterface.findAll());

        return new ModelAndView("sections/index");
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboardPage(Principal user, Model model)
    {
        if(user.getName().equals("null"))
            return new ModelAndView("redirect:/");

        else
        {
            model.addAttribute("dbUser", userInterface.findByUsername(user.getName()));
            model.addAttribute("userObject", new User());
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);

            User user1 = userInterface.findByUsername(user.getName());

            for(int i = 0; i < user1.getProjects().size(); i++)
            {
                Project project1 = projectInterface.findByTitle(user1.getProjects().get(i).getTitle());

                if(project1 != null)
                {
                    project1.setAuthor(user1);

                    projectInterface.save(project1);
                }
            }

            return new ModelAndView("sections/dashboard");
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
                    .antMatchers("/").permitAll().and()
                    .authorizeRequests()
                    .anyRequest().authenticated();
        }
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession httpSession) {
        httpSession.invalidate();
        return new ModelAndView("redirect:/");
    }
}
