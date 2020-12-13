package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    @PostMapping(value = "/addNewSkill", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/removeSkill", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * This request removes a notification
     *
     * @param data is information that the users sends, which contains user's jwt & notification unique key
     * @return response if the notification has been removed successfully
     */
    @PostMapping(value = "/removeNotification", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response removeNotification(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String notificationKey = data.get("notificationKey");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        user.getNotifications().getValue().remove(notificationKey);
        userService.save(user);

        return Response.OK;
    }
}