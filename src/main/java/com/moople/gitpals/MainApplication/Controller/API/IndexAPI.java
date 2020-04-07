package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/api/index")
public class IndexAPI {

    @Autowired
    private KeyStorageInterface keyStorageInterface;

    @GetMapping("/test")
    public Response response() {
        return Response.OK;
    }

    @GetMapping("/getAuthKey")
    public String keyStorage(Principal user) {
        try {
            return "Your key: " + keyStorageInterface.findByUsername(user.getName()).getKey();
        } catch (Exception e) {
            return "You should be authorized";
        }
    }
}