package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class UserController
{
    @Autowired
    userInterface userInterface;


    @GetMapping("/users/{username}")
    public ModelAndView findUser(@PathVariable String username, Model model)
    {
        model.addAttribute("UserObject", userInterface.findByUsername(username));

        return new ModelAndView("sections/userDashboard");
    }

    @PostMapping("/updateUser")
    public ModelAndView updateTechs(Principal user, @RequestParam("techCheckbox") List<String> techs)
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

        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/updateUserCountry")
    public ModelAndView updateCountry(@RequestParam("countryInput") String country, @RequestParam("infoInput") String info, Principal user)
    {
        User userInDB = userInterface.findByUsername(user.getName());

        userInDB.setCountry(country);
        userInDB.setInfo(info);

        userInterface.save(userInDB);

        return new ModelAndView("redirect:/dashboard");
    }
}