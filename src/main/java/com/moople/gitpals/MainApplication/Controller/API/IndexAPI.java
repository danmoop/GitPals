package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/index")
public class IndexAPI {

    /**
     * This is a simple function just to ping the website
     *
     * @return OK Response
     */
    @GetMapping("/test")
    public Response response() {
        return Response.OK;
    }
}