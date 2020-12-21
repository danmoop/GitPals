package com.moople.gitpals.MainApplication.model;

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
    private String mobileAuthPassword;
    private Set<String> skillList;
    private Map<String, Pair<Integer, List<Message>>> dialogs;
    private List<String> submittedProjects;
    private List<String> projectsAppliedTo;
    private Pair<Integer, Map<String, Notification>> notifications;
    private long lastOnlineDate;
    private boolean banned;
    private boolean hasSeenGlobalMessage;
    private boolean isAdmin;

    public User(String username, String githubAccountLink, String email, String country, String bio) {
        this.username = username.trim();
        this.githubAccountLink = githubAccountLink.trim();
        this.skillList = new HashSet<>();

        this.email = email == null ? null : email.trim();
        this.country = country == null ? null : country.trim();
        this.bio = bio == null ? null : bio.trim();

        this.dialogs = new HashMap<>();
        this.submittedProjects = new ArrayList<>();
        this.projectsAppliedTo = new ArrayList<>();
        this.notifications = new Pair<>(0, new HashMap<>());

        this.hasSeenGlobalMessage = false;
        this.banned = false;
        this.isAdmin = false;

        this.mobileAuthPassword = "";
        this.lastOnlineDate = new Date().getTime();
    }
}