package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private KeyStorageInterface keyStorageInterface;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * This function adds a skill to a user's skill list
     *
     * @param data is a json object, which contains a user's jwt & a skill, which will be added
     * @return a response if all went ok
     */
    @PostMapping("/addNewSkill")
    public Response addNewSkill(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String skill = (String) data.get("skill");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.USER_NOT_FOUND;
        }

        user.getSkillList().add(skill);
        userService.save(user);

        return Response.OK;
    }

    /**
     * This function removes a skill from a user's skill list
     *
     * @param data is a json object, which contains a user's jwt & a skill, which will be removed
     * @return a response if all went ok
     */
    @PostMapping("/removeSkill")
    public Response removeSkill(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String skill = (String) data.get("skill");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.USER_NOT_FOUND;
        }

        user.getSkillList().remove(skill);
        userService.save(user);

        return Response.OK;
    }
}