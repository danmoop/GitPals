package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.model.Response;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RestController
@CrossOrigin
@RequestMapping("/api/index")
public class IndexAPI {

    /**
     * This is a simple function just to ping the website
     *
     * @return OK Response
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response response() {
        return Response.OK;
    }
}
