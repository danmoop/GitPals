package com.moople.gitpals.MainApplication.service.interfaces;

import java.util.List;

public interface AdminInterface {
    boolean removeAllUserProjects(String username);

    boolean makeUserAnAdmin(String username);

    void deleteAllForumPostsByUser(String username);

    void modifyGlobalAlert(String text);

    List<String> getActiveDailyUsers();

    List<String> getActiveWeeklyUsers();
}