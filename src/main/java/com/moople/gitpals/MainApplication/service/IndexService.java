package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.Message;
import com.moople.gitpals.MainApplication.model.Pair;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.interfaces.IndexInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService implements IndexInterface {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private KeyStorageService keyStorageService;

    @Autowired
    private GlobalMessageService globalMessageService;

    private final long ONE_DAY = 1000 * 86400;
    private final int PROJECTS_PER_PAGE = 16;

    /**
     * every page has a fixed number of projects displayed
     *
     * @return total number of pages that will be displayed on index page
     */
    @Override
    public int getNumberOfPages() {

        int numberOfPages = projectService.findAll().size() / PROJECTS_PER_PAGE;
        numberOfPages = numberOfPages == 0 ? 1 : numberOfPages;

        if (projectService.findAll().size() - numberOfPages * PROJECTS_PER_PAGE > 0) {
            numberOfPages += 1;
        }

        return numberOfPages;
    }

    /**
     * This function counts a number of new unread messages received by another users
     *
     * @param user is a user object in the database
     * @return number of messages that are unread
     */
    @Override
    public int countUnreadMessages(User user) {
        int unreadMessages = 0;

        for (Map.Entry<String, Pair<Integer, List<Message>>> entry : user.getDialogs().entrySet()) {
            unreadMessages += entry.getValue().getKey();
        }

        return unreadMessages;
    }

    /**
     * This function return a list of projects displayed on specific page
     * Each page has different projects
     *
     * @param page is a page number specified by a user
     * @return list of projects displayed on page specified by a user
     */
    @Override
    public List<Project> getProjectsOnPage(int page) {
        List<Project> allProjects = projectService.findAll();
        int projectsAmount = allProjects.size();

        List<Project> projects;

        if (projectsAmount <= PROJECTS_PER_PAGE) {
            projects = allProjects;
            Collections.reverse(projects);
        } else {
            projects = new ArrayList<>();

            int start = projectsAmount - 1 - (PROJECTS_PER_PAGE * (page - 1));
            int end = projectsAmount - (PROJECTS_PER_PAGE * page);
            end = Math.max(end, 0);

            for (int i = start; i >= end; i--) {
                projects.add(allProjects.get(i));
            }
        }

        return projects;
    }

    /**
     * This function checks if data in user's GitHub profile has changed
     * If so, data will also change in the user's GitPals profile
     *
     * @param userDB     is a user object from the database
     * @param properties is information extracted from GitHub profile
     */
    @Override
    public void checkIfDataHasChanged(User userDB, LinkedHashMap<String, Object> properties) {

        boolean shouldSaveChanges = false;

        // Email
        if (properties.get("email") == null) {
            if (userDB.getEmail() != null) {
                userDB.setEmail(null);
                shouldSaveChanges = true;
            }
        } else {
            if (userDB.getEmail() == null || !userDB.getEmail().equals(properties.get("email").toString())) {
                userDB.setEmail(properties.get("email").toString());
                shouldSaveChanges = true;
            }
        }

        // Location
        if (properties.get("location") == null) {
            if (userDB.getCountry() != null) {
                userDB.setCountry(null);
                shouldSaveChanges = true;
            }
        } else {
            if (userDB.getCountry() == null || !userDB.getCountry().equals(properties.get("location").toString())) {
                userDB.setCountry(properties.get("location").toString());
                shouldSaveChanges = true;
            }
        }

        // Bio
        if (properties.get("bio") == null) {
            if (userDB.getBio() != null) {
                userDB.setBio(null);
                shouldSaveChanges = true;
            }
        } else {
            if (userDB.getBio() == null || !userDB.getBio().equals(properties.get("bio").toString())) {
                userDB.setBio(properties.get("bio").toString());
                shouldSaveChanges = true;
            }
        }

        if (!properties.get("avatar_url").toString().equals(userDB.getAvatarURL())) {
            userDB.setAvatarURL(properties.get("avatar_url").toString());
            shouldSaveChanges = true;
        }

        // Last Online Date (update if there is 1day difference to avoid multiple database updates on the same day)
        long currentTime = new Date().getTime();
        if (currentTime - userDB.getLastOnlineDate() >= ONE_DAY) {
            userDB.setLastOnlineDate(currentTime);
            shouldSaveChanges = true;
        }

        if (shouldSaveChanges) {
            userService.save(userDB);
        }
    }
}