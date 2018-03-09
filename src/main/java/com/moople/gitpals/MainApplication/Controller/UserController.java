package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class UserController
{
    @Autowired
    userInterface userInterface;

    @PostMapping("/updateUser")
    public ModelAndView updateTechs(@ModelAttribute User user, @RequestParam("techCheckbox") List<String> techs)
    {

        User userFromDB = userInterface.findByUsername(user.getUsername());

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
}
