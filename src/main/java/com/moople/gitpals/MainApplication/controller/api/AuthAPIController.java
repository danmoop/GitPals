package com.moople.gitpals.MainApplication.controller.api;

import com.moople.gitpals.MainApplication.configuration.GitPalsUserDetails;
import com.moople.gitpals.MainApplication.configuration.JWTUtil;
import com.moople.gitpals.MainApplication.model.AuthRequest;
import com.moople.gitpals.MainApplication.model.AuthResponse;
import com.moople.gitpals.MainApplication.model.Response;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.repository.UserRepository;
import com.moople.gitpals.MainApplication.tools.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthAPIController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GitPalsUserDetails userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * This function checks a request sent by a user and creates a jwt token for the user
     *
     * @param request contains username and password (key) so the system can identify the user
     * @return jwt token
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createToken(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), Encrypt.MD5(request.getPassword()))
            );
        } catch (Exception e) {
            return ResponseEntity.ok(Response.FAILED);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    /**
     * This function returns a user by their jwt token
     *
     * @param jwt is a user's auth token
     * @return user
     */
    @GetMapping(value = "/getUserByJwt/{jwt}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserByJwt(@PathVariable String jwt) {
        return userRepository.findByUsername(jwtUtil.extractUsername(jwt));
    }
}