package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
}