package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * This function finds users based on their skills
     *
     * @param skills is a list of skills a user should have in order to be found
     * @return list of users who have skills enumerated in the list
     */
    @Override
    public Set<String> findBySkillList(List<String> skills) {
        Set<String> users = new HashSet<>();

        for (User user : userInterface.findAll()) {
            Set<String> userSkills = user.getSkillList().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            for (String skill : skills) {
                if (userSkills.contains(skill.toLowerCase())) {
                    users.add(user.getUsername());
                    break;
                }
            }
        }

        return users;
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