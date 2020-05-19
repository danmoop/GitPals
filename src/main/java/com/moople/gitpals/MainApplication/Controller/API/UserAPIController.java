package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.KeyStorage;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
     * This function changes the user's skill set
     *
     * @param data is an object, which contains user's current skill set and token
     * @return status, which depends on the success of the user's object change
     */
    @PostMapping(value = "/setSkillList", produces = "application/json")
    public Response setSkillList(@RequestBody Map<Object, Object> data) {
        try {
            Map<String, Boolean> skillMap = (Map<String, Boolean>) data.get("techs");
            String token = (String) data.get("token");

            User user = userService.findByUsername(jwtUtil.extractUsername(token));

            user.setSkillList(skillMap);
            userService.save(user);

            return Response.OK;
        } catch (Exception e) {
            return Response.FAILED;
        }
    }

    /**
     * This request displays the user's personal key for authentication via mobile phone
     *
     * @param user is a user's authentication
     * @return user's auth key
     */
    @GetMapping("/requestAuthKey")
    public Map<String, String> getAuthKey(Principal user) {
        HashMap<String, String> result = new HashMap<>();
        if (user == null) {
            result.put("message", "You are not logged in. Please sign in to obtain your key");
            return result;
        } else {
            String key = keyStorageInterface.findByUsername(user.getName()).getKey();
            if (key == null) {
                KeyStorage ks = new KeyStorage(user.getName());
                keyStorageInterface.save(ks);
                result.put("your-key", ks.getKey());
                return result;
            }
            result.put("your-key", key);
            return result;
        }
    }
}