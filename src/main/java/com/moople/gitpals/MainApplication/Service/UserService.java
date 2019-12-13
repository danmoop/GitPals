package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserInterface userInterface;

    @Override
    public User findByUsername(String username) {
        return userInterface.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userInterface.findAll();
    }

    @Override
    public List<User> findBySkillList(List<String> skills) {
        return null;
    }

    @Override
    public void save(User user) {
        userInterface.save(user);
    }
}

interface UserServiceInterface {
    User findByUsername(String username);
    List<User> findAll();
    List<User> findBySkillList(List<String> skills);
    void save(User user);
}