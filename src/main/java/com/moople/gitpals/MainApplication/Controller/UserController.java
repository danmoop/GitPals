package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.userInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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


      /*  for(int i = 0; i < techs.size(); i++)
        {
            for(int q = 0; q < allTechs.size(); q++)
            {
                if(techs.get(i).equals(allTechs.keySet().toArray()[q]))
                {
                    System.out.println(allTechs.keySet().toArray()[q]);

                    allTechs.put(String.valueOf(allTechs.keySet().toArray()[q]), true);
                }

                else if(!allTechs.keySet().toArray()[q].equals(techs.get(i)))
                    allTechs.put(String.valueOf(allTechs.keySet().toArray()[q]), false);

            }
        }*/

        for(int i = 0; i < allTechs.size(); i++)
        {
            for(int q = 0; q < techs.size(); q++)
            {
                if(allTechs.keySet().toArray()[i].equals(techs.get(q)))
                    allTechs.put(String.valueOf(allTechs.keySet().toArray()[i]), true);

                else
                {
                    if(q == techs.size())
                    {
                        allTechs.put(String.valueOf(allTechs.keySet().toArray()[i]), false);
                    }
                }
            }
        }

        userInterface.save(userFromDB);

        return new ModelAndView("redirect:/dashboard");
    }
}
