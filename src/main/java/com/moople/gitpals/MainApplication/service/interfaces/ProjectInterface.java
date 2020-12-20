package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.Comment;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;

import java.util.List;
import java.util.Set;

public interface ProjectInterface {
    Project findByTitle(String title);

    Project findById(String id);

    List<Project> findAll();

    List<Project> getFixedNumberOfProjects(int amount);

    List<Project> matchProjectsByProjectTitle(String title);

    List<Project> matchProjectsByTechnologies(List<String> technologies);

    List<Project> matchProjectsByRoles(List<String> roles);

    void editProjectInfo(Project project, String title, String description, String repoLink, Set<String> technologies, Set<String> roles);

    void sendComment(Project project, Comment comment, User user);

    void editComment(Project project, String text, String commentKey, String username);

    void deleteProject(Project project, User user);

    void save(Project project);

    void delete(Project project);

    boolean removeComment(Project project, String username, String commentText);
}
