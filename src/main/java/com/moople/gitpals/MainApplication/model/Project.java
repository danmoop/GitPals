package com.moople.gitpals.MainApplication.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    private String title;
    private String description;
    private String githubProjectLink;
    private String authorName;
    private Set<String> technologies;
    private Set<String> appliedUsers;
    private Set<String> requiredRoles;
    private Set<String> likes;
    private Map<String, Comment> comments;

    public Project(String title, String description, String githubProjectLink, String authorName, Set<String> technologies, Set<String> requiredRoles) {
        this.title = title.trim();
        this.description = description.trim();
        this.githubProjectLink = githubProjectLink.trim();

        this.authorName = authorName;
        this.technologies = technologies;
        this.requiredRoles = requiredRoles;

        this.comments = new HashMap<>();
        this.appliedUsers = new HashSet<>();
        this.likes = new HashSet<>();
    }
}