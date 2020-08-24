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
     * This function changes the user's skill set
     *
     * @param data is an object, which contains user's current skill set and token
     * @return status, which depends on the success of the user's object change
     */
    @PostMapping(value = "/setSkillList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response setSkillList(@RequestBody Map<Object, Object> data) {
        return Response.OK;
    }
}