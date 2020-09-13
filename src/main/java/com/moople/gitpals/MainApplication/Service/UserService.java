package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is not really necessary, I could easily use (next line)
 *
 * @see UserInterface
 * however, this class is necessary because there is method findBySkillList()
 * it has no implementation by default (like find by name/key/title etc.), so that is what this class is for
 */
@Service
public class UserService {

    @Autowired
    private UserInterface userInterface;

    /**
     * This function finds a user by the username
     *
     * @param username is a username, by which a user object will be found and returned
     * @return a user object
     */
    public User findByUsername(String username) {
        return userInterface.findByUsername(username);
    }

    /**
     * This function returns all the users registered from the database
     *
     * @return all registered users
     */
    public List<User> findAll() {
        return userInterface.findAll();
    }

    /**
     * This function finds users based on their skills
     *
     * @param skills is a list of skills a user should have in order to be found
     * @return list of users who have skills enumerated in the list
     */
    public Set<String> findBySkillList(List<String> skills) {
        Set<String> users = new HashSet<>();

        for (User user : userInterface.findAll()) {
            for (String skill : skills) {
                user.getSkillList().forEach(userSkill -> {
                    if (userSkill.toLowerCase().contains(skill.toLowerCase())) {
                        users.add(user.getUsername());
                    }
                });
            }
        }

        return users;
    }

    /**
     * This function saves a user into the database
     * If user doesn't exist in the database, their object will be created
     * If user exists in the database, it will overwrite the existing information with some changes that occurred
     *
     * @param user is a user's object that will be written to the database
     */
    public void save(User user) {
        userInterface.save(user);
    }
}