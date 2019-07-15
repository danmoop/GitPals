package com.moople.gitpals.MainApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "projects")
public class Project
{
    @Id
    private String id;

    private String title;
    private String description;
    private String githubProjectLink;
    private String authorName;
    private List<String> requirements;
    private List<String> usersSubmitted;

    /**
     * @param usersSubmitted is a list of objects (users' names), empty by default.
     */
    public Project(String title, String description, String githubProjectLink, String authorName, List<String> requirements)
    {
        this.title = title;
        this.description = description;
        this.githubProjectLink = githubProjectLink;
        this.authorName = authorName;
        this.requirements = requirements;
        this.usersSubmitted = new ArrayList<>();
    }
}