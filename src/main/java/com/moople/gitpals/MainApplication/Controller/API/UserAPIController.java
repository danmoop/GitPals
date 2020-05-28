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
}