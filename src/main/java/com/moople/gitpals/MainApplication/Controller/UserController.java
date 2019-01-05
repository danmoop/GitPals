package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.userInterface;
import com.moople.gitpals.MainApplication.Service.projectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class UserController
{
    @Autowired
    userInterface userInterface;

    @Autowired
    projectInterface projectInterface;

    @GetMapping("/users/{username}")
    public String findUser(@PathVariable String username, Model model)
    {
        User user = userInterface.findByUsername(username);

        if(user != null)
        {
            List<Project> appliedToProjects = new ArrayList<>();

            for (int i = 0; i < user.getAppliedTo().size(); i++)
            {
                appliedToProjects.add(projectInterface.findByTitle(user.getAppliedTo().get(i)));
            }

            model.addAttribute("UserObject", userInterface.findByUsername(username));
            model.addAttribute("appliedProjects", appliedToProjects);


            return "sections/userDashboard";
        }

        else
        {
            model.addAttribute("user_name", username);
            return "error/userNotFound";
        }
    }

    @PostMapping("/updateUser")
    public String updateTechs(Principal user, @RequestParam("techCheckbox") List<String> techs)
    {
        User userFromDB = userInterface.findByUsername(user.getName());

        Map<String, Boolean> allTechs = userFromDB.getLanguagesKnows();

        for (Map.Entry<String, Boolean> entry : allTechs.entrySet())
        {
            allTechs.put(entry.getKey(), false);
        }

        for (Iterator<String> i = techs.iterator(); i.hasNext();) {
            String item = i.next();
            allTechs.put(item, true);
        }

        userInterface.save(userFromDB);

        return "redirect:/dashboard";
    }

    @PostMapping("/updateUserCountry")
    public String updateCountry(@RequestParam("countryInput") String country, @RequestParam("infoInput") String info, Principal user)
    {
        User userInDB = userInterface.findByUsername(user.getName());

        userInDB.setCountry(country);
        userInDB.setInfo(info);

        userInterface.save(userInDB);

        return "redirect:/dashboard";
    }
}