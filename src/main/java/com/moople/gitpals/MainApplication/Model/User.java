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
    private String GithubAccountLink;
    private String country;
    private String info;
    private Map<String, Boolean> languagesKnows;
    private List<String> projects;
    private List<String> projectsAppliedTo;
    private List<Message> messages;

    /**
     * @param country   & info are empty by default. Later they can be edited in dashboard.
     * @param projects, projectsAppliedTo & messages are lists of objects, empty by default.
     */
    public User(String username, String githubAccountLink, Map<String, Boolean> languagesKnows) {
        this.username = username;
        this.GithubAccountLink = githubAccountLink;
        this.languagesKnows = languagesKnows;

        this.country = "";
        this.info = "";

        this.projects = new ArrayList<>();
        this.projectsAppliedTo = new ArrayList<>();
        this.messages = new ArrayList<>();
    }
}