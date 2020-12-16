package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Pair;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
     * This function returns a user by username
     *
     * @param username is a user's username
     * @return user object
     */
    @GetMapping("/get/{username}")
    public User getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return Data.EMPTY_USER;
        }

        // It is not safe to send user's messages/notifications messages to anyone, so remove them
        user.setNotifications(null);
        user.setDialogs(null);

        return user;
    }

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

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
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

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        user.getSkillList().remove(skill);
        userService.save(user);

        return Response.OK;
    }


    /**
     * This function marks a dialog with some user as 'seen', so its won't say it has new message anymore
     *
     * @param data is information sent from the user, which contains user's jwt and dialog name (the user they talk to)
     * @return a response, which is OK if all the data sent from the user is valid
     */
    @PostMapping(value = "/markDialogAsSeen", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response markDialogAsSeen(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String dialogName = data.get("dialogName");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        user.getDialogs().get(dialogName).setKey(0);
        userService.save(user);

        return Response.OK;
    }


    /**
     * This function marks all user's notifications as 'seen'
     *
     * @param data is information sent from the user, which contains user's jwt
     * @return a response, which is OK if all the data sent from the user is valid
     */
    @PostMapping(value = "/markNotificationsAsSeen", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response markNotificationsAsSeen(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        user.getNotifications().setKey(0);
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

    /**
     * This request removes all user's notifications
     *
     * @param data is information sent from the user, which contains user's jwt token
     * @return response if all notifications have been deleted successfully
     */
    @PostMapping("/removeAllNotifications")
    public Response removeAllNotifications(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        user.setNotifications(new Pair<>(0, new HashMap<>()));
        userService.save(user);

        return Response.OK;
    }

    /**
     * This function return a user's unique key, which is used for websocket chat communication
     * This key acts as a destination (so message goes to the right person, destination is based on this key)
     *
     * @param jwt is user's jwt token
     * @return user's message key
     */
    @GetMapping("/getMessageKey/{jwt}")
    public Map<String, String> getMessageKey(@PathVariable String jwt) {
        Map<String, String> data = new HashMap<>();

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return data;
        }

        data.put("key", keyStorageInterface.findByUsername(user.getUsername()).getKey());

        return data;
    }
}