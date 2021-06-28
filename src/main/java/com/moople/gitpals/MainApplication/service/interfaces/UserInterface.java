package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.User;

import java.util.List;
import java.util.Set;

public interface UserInterface {
    User findByUsername(String username);

    List<User> findAll();

    List<User> matchUsersByUsername(String username);

    Set<String> findBySkillList(List<String> skills);

    boolean removeNotification(String username, String notificationKey);

    boolean removeAllNotifications(String username);

    void save(User user);

    void delete(User user);
}