package com.moople.gitpals.MainApplication.Configuration;

import com.moople.gitpals.MainApplication.Service.KeyStorageInterface;
import com.moople.gitpals.MainApplication.Service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class GitPalsUserDetails implements UserDetailsService {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private KeyStorageInterface keyStorageInterface;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        com.moople.gitpals.MainApplication.Model.User user = userInterface.findByUsername(s);

        if (user == null) {
            throw new UsernameNotFoundException("No user with username " + s);
        }

        String key = keyStorageInterface.findByUsername(s).getKey();
        return new User(user.getUsername(), key, new ArrayList<>());
    }
}