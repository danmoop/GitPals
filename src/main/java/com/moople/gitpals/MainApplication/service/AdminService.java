package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.GlobalMessage;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.interfaces.AdminInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements AdminInterface {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private GlobalMessageService globalMessageService;

    private final long ONE_DAY = 84600 * 1000;
    private final long ONE_WEEK = ONE_DAY * 7;

    @Override
    public boolean removeAllUserProjects(String username) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return false;
        }

        user.getSubmittedProjects().stream()
                .map(project -> projectService.findByTitle(project))
                .forEach(project -> {
                    projectService.delete(project);
                });

        user.getSubmittedProjects().clear();
        userService.save(user);

        return true;
    }

    @Override
    public boolean makeUserAnAdmin(String username) {

        User user = userService.findByUsername(username);

        if (user == null) {
            return false;
        }

        user.setAdmin(!user.isAdmin());
        userService.save(user);

        return true;
    }

    @Override
    public void deleteAllForumPostsByUser(String username) {
        forumService.findAll().stream()
                .filter(post -> post.getAuthor().equals(username))
                .forEach(post -> forumService.delete(post));
    }

    @Override
    public void modifyGlobalAlert(String text) {

        List<GlobalMessage> globalMessages = globalMessageService.findAll();

        GlobalMessage globalMessage;
        if (globalMessages.size() == 0) {
            globalMessage = new GlobalMessage(text);
        } else {
            globalMessage = globalMessages.get(0);
            globalMessage.setContent(text);
        }
        globalMessageService.save(globalMessage);

        userService.findAll().forEach(user -> {
            user.setHasSeenGlobalMessage(false);
            userService.save(user);
        });
    }

    @Override
    public List<String> getActiveDailyUsers() {
        long currentTime = new Date().getTime();

        return userService.findAll()
                .stream().filter(user -> currentTime - user.getLastOnlineDate() <= ONE_DAY)
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getActiveWeeklyUsers() {
        long currentTime = new Date().getTime();

        return userService.findAll()
                .stream().filter(user -> currentTime - user.getLastOnlineDate() <= ONE_WEEK)
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}