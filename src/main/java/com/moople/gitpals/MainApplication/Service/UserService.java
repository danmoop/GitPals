package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Set<String> findBySkillList(List<String> skills) {
        Set<String> ret = new HashSet<>();

        for (User user : userInterface.findAll()) {
            for (String skill : skills) {
                if (user.getSkillList().get(skill)) {
                    ret.add(user.getUsername());
                }
            }
        }

        return ret;
    }

    @Override
    public void save(User user) {
        userInterface.save(user);
    }
}

interface UserServiceInterface {
    User findByUsername(String username);

    List<User> findAll();

    Set<String> findBySkillList(List<String> skills);

    void save(User user);
}