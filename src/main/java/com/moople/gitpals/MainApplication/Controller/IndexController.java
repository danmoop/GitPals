package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.User;
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

@RestController
public class IndexController
{

    @Autowired
    userInterface userInterface;

    @GetMapping("/")
    public ModelAndView indexPage(Principal user, Model model)
    {
        if(user != null)
        {
            System.out.println(user.getName());
            model.addAttribute("GithubUserName", user.getName());
            model.addAttribute("GithubUser", user);

            if(userInterface.findByUsername(user.getName()).getUsername().equals("null"))
            {
                userInterface.save(
                        new User(
                                user.getName(),
                                "https://github.com/" + user.getName(),
                                "None",
                                new ArrayList<>()
                        )
                );
            }
        }

        return new ModelAndView("sections/index");
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
