package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @PostMapping("/addNewSkill")
    public Response addNewSkill(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String skill = (String) data.get("skill");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if(user == null) {
            return Response.USER_NOT_FOUND;
        }

        user.getSkillList().add(skill);
        userService.save(user);

        return Response.OK;
    }

    @PostMapping("/removeSkill")
    public Response removeSkill(@RequestBody Map<String, Object> data) {
        String jwt = (String) data.get("jwt");
        String skill = (String) data.get("skill");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if(user == null) {
            return Response.USER_NOT_FOUND;
        }

        user.getSkillList().remove(skill);
        userService.save(user);

        return Response.OK;
    }
}