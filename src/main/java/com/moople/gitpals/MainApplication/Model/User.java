package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "users")

public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String country;
    private String bio;
    private String GithubAccountLink;
    private Map<String, Boolean> skillList;
    private List<String> projects;
    private List<String> projectsAppliedTo;
    private List<Message> messages;
    private boolean notificationsEnabled;

    /**
     * @param country   & info are taken from GitHub account
     * @param projects, projectsAppliedTo & messages are lists of objects, empty by default.
     */
    public User(String username, String githubAccountLink, Map<String, Boolean> skillList, String email, String country, String bio) {
        this.username = username;
        this.GithubAccountLink = githubAccountLink;
        this.skillList = skillList;
        this.email = email;
        this.country = country;
        this.bio = bio;

        this.projects = new ArrayList<>();
        this.projectsAppliedTo = new ArrayList<>();
        this.messages = new ArrayList<>();

        this.notificationsEnabled = true;
    }
}