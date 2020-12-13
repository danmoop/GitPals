package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/messages")
public class MessageAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

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
}