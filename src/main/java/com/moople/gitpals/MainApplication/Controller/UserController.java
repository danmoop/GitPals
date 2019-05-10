package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
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
    private UserInterface userInterface;

    @Autowired
    private ProjectInterface projectInterface;

    /*
        @param username is taken from an address field - like "/users/danmoop"
        @param model & principal are assigned automatically by spring
        @return user's dashboard html page with all the data about the user
     */
    @GetMapping("/users/{username}")
    public String findUser(@PathVariable String username, Model model, Principal principal)
    {
        User user = userInterface.findByUsername(username);

        if(user != null)
        {
            List<Project> appliedToProjects = new ArrayList<>();

            for (int i = 0; i < user.getAppliedTo().size(); i++)
            {
                appliedToProjects.add(projectInterface.findByTitle(user.getAppliedTo().get(i)));
            }

            try {
                model.addAttribute("LoggedUser", principal.getName());
            } catch (NullPointerException e) {
                model.addAttribute("LoggedUser", null);
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

    /*
        @param techs are taken from html form, they are technologies checkboxes users select in their dashboard
        @param user is assigned automatically by spring
        @return redirect to the same page with new data
     */
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

            if(allTechs.get(item) != null)
                allTechs.put(item, true);
        }

        userInterface.save(userFromDB);

        return "redirect:/dashboard";
    }


    /*
        @param country & info are taken from html input fields
        @param principal user is used to find user in database, set new country & info
        @return redirect to the same page with new data
     */
    @PostMapping("/updateUserCountry")
    public String updateCountry(
            @RequestParam("countryInput") String country,
            @RequestParam("infoInput") String info,
            Principal user)
    {
        User userInDB = userInterface.findByUsername(user.getName());

        userInDB.setCountry(country);
        userInDB.setInfo(info);

        userInterface.save(userInDB);

        return "redirect:/dashboard";
    }
}