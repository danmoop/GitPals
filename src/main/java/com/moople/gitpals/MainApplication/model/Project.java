package com.moople.gitpals.MainApplication.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private List<Comment> comments;

    public Project(String title, String description, String githubProjectLink, String authorName, Set<String> technologies, Set<String> requiredRoles) {
        this.title = title.trim();
        this.description = description.trim();
        this.githubProjectLink = githubProjectLink.trim();

        this.authorName = authorName;
        this.technologies = technologies;
        this.requiredRoles = requiredRoles;

        this.comments = new ArrayList<>();
        this.appliedUsers = new HashSet<>();
    }
}