package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;

import java.util.LinkedHashMap;
import java.util.List;

public interface IndexInterface {
    int getNumberOfPages();

    int countUnreadMessages(User user);

    List<Project> getProjectsOnPage(int page);

    void checkIfDataHasChanged(User userDB, LinkedHashMap<String, Object> properties);
}