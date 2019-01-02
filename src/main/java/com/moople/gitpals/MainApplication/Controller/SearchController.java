package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController
{
    @Autowired
    userInterface userInteface;

    @Autowired
    projectInterface projectInteface;

    @GetMapping("/search")
    public String searchPage()
    {
        return "sections/searchForm";
    }

    @PostMapping("/findUser")
    public String foundUsers(@RequestParam("user_name") String username, Model model)
    {
        List<User> allUsers = userInteface.findAll();

        List<User> matchUsers = new ArrayList<>();

        for(int i = 0; i < allUsers.size(); i++)
        {
            if(allUsers.get(i).getUsername().contains(username))
            {
                matchUsers.add(allUsers.get(i));
            }
        }

        model.addAttribute("match_users", matchUsers);

        return "sections/matchUsers";
    }

    @PostMapping("/findProject")
    public String foundProjects(@RequestParam("project_name") String projectname, Model model)
    {

        List<Project> allProjects = projectInteface.findAll();

        List<Project> matchProjects = new ArrayList<>();

        for(int i = 0; i < allProjects.size(); i++)
        {
            if(allProjects.get(i).getTitle().contains(projectname))
            {
                matchProjects.add(allProjects.get(i));
            }
        }

        model.addAttribute("match_projects", matchProjects);

        return "sections/matchProjects";
    }
}