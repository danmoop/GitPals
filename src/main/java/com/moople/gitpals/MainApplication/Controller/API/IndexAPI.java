package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/index")
public class IndexAPI {

    @GetMapping("/test")
    public Response response() {
        return Response.OK;
    }
}