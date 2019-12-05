package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserAPIController {

    @Autowired
    private UserInterface userInterface;

    /**
     * Get user object which contains username, projects, skills, etc, (fetched from db)
     *
     * @param username is a path variable - username of person who we try to find
     * @return user object if exists
     */
    @GetMapping("/getUser/{username}")
    public User getUser(@PathVariable("username") String username) {
        User user = userInterface.findByUsername(username);

        if(user != null) {
            return user;
        }

        return new User();
    }

    /**
     * This request handles user's info change
     *
     * @param map is a data object sent from frontend, contains new user's
     * techs list, updated country and info
     *
     * @return response whether info has been updated
     */
    @PostMapping("/updateUser")
    public Response updateUser(@RequestBody Map<Object, Object> map) {
        String username = (String) map.get("username");
        String userCountry = (String) map.get("country");
        String info = (String) map.get("info");

        Map<String, Boolean> techs = (Map<String, Boolean>) map.get("techs");

        User user = userInterface.findByUsername(username);

        if(user != null) {
            user.setLanguagesKnows(techs);
            user.setCountry(userCountry);
            user.setInfo(info);

            return Response.OK;
        }

        return Response.FAILED;
    }
}

