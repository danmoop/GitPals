package com.moople.gitpals.MainApplication.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "projects")
public class Project
{
    @Id
    private String id;

    private String title;
    private String description;
    private String githubProjectLink;
    private User author;
    private List<String> requirements;
    private List<User> usersSubmitted;

    public Project(String title, String description, String githubProjectLink, User author, List<String> requirements, List<User> usersSubmitted)
    {
        this.title = title;
        this.description = description;
        this.githubProjectLink = githubProjectLink;
        this.author = author;
        this.requirements = requirements;
        this.usersSubmitted = usersSubmitted;
    }

    public Project(){}

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getGithubProjectLink()
    {
        return githubProjectLink;
    }

    public void setGithubProjectLink(String githubProjectLink)
    {
        this.githubProjectLink = githubProjectLink;
    }

    public List<User> getUsersSubmitted()
    {
        return usersSubmitted;
    }

    public User getAuthor()
    {
        return author;
    }

    public void setAuthor(User author)
    {
        this.author = author;
    }

    public List<String> getRequirements()
    {
        return requirements;
    }

    public void setRequirements(List<String> requirements)
    {
        this.requirements = requirements;
    }

    public void addAppliedUser(User user)
    {
        usersSubmitted.add(user);
    }

    public void deleteAppliedUser(User user)
    {
        usersSubmitted.remove(user);
    }
}