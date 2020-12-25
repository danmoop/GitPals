package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.Pair;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.repository.UserRepository;
import com.moople.gitpals.MainApplication.service.interfaces.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserInterface {

    @Autowired
    private UserRepository userRepository;

    /**
     * This function finds a user by the username
     *
     * @param username is a username, by which a user object will be found and returned
     * @return a user object
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * This function returns all the users registered from the database
     *
     * @return all registered users
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * This function returns a list of users whose username matches the input
     *
     * @param username is a username we pass in path
     * @return list of users whose username match the one we pass
     */
    @Override
    public List<User> matchUsersByUsername(String username) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .peek(user -> {
                    user.setDialogs(null);
                    user.setNotifications(null);
                })
                .collect(Collectors.toList());
    }

    /**
     * This function finds users based on their skills
     *
     * @param skills is a list of skills a user should have in order to be found
     * @return list of users who have skills enumerated in the list
     */
    @Override
    public Set<String> findBySkillList(List<String> skills) {
        return userRepository.findAll().stream()
                .filter(user -> skills
                        .stream()
                        .anyMatch(skill -> user.getSkillList()
                                .stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.toList())
                                .contains(skill.toLowerCase())))
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }

    /**
     * This request removes all user's notifications
     *
     * @param username        is a user's name, whose notification will be removed
     * @param notificationKey is a unique key so we could find it in the database
     */
    @Override
    public void removeNotification(String username, String notificationKey) {
        User userDB = findByUsername(username);

        userDB.getNotifications().getValue().remove(notificationKey);
        save(userDB);
    }

    /**
     * This request removes all user's notifications
     *
     * @param username is a user's name, whose notifications will be removed
     */
    @Override
    public void removeAllNotifications(String username) {
        User userDB = findByUsername(username);

        userDB.setNotifications(new Pair<>(0, new HashMap<>()));
        save(userDB);
    }

    /**
     * This function saves a user into the database
     * If user doesn't exist in the database, their object will be created
     * If user exists in the database, it will overwrite the existing information with some changes that occurred
     *
     * @param user is a user's object that will be written to the database
     */
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * This function removes a user from the database
     *
     * @param user is a user who will be removed from the database
     */
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}