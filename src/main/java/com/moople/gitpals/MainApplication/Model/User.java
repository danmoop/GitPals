package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@NoArgsConstructor
@ToString
@Document(collection = "users")

public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String country;
    private String bio;
    private String githubAccountLink;
    private String avatarURL;
    private String timezone;
    private Set<String> skillList;
    private Map<String, DialogPair> dialogs;
    private List<String> submittedProjects;
    private List<String> projectsAppliedTo;
    private long lastOnlineDate;
    private boolean banned;
    private boolean hasSeenGlobalMessage;
    private boolean isAdmin;

    /**
     * @param country & info are taken from GitHub account
     */
    public User(String username, String githubAccountLink, String email, String country, String bio) {
        this.username = username;
        this.githubAccountLink = githubAccountLink;
        this.skillList = new HashSet<>();
        this.email = email;
        this.country = country;
        this.bio = bio;

        this.dialogs = new HashMap<>();
        this.submittedProjects = new ArrayList<>();
        this.projectsAppliedTo = new ArrayList<>();

        this.hasSeenGlobalMessage = false;
        this.banned = false;
        this.isAdmin = false;

        this.lastOnlineDate = new Date().getTime();
    }
}