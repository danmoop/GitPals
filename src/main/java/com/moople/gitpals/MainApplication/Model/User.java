package com.moople.gitpals.MainApplication.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "users")
public class User
{
    @Id
    private String id;

    private String username;
    private String GithubAccountLink;
    private String country;
    private String info;
    private Map<String, Boolean> languagesKnows;
    private List<String> projects;
    private List<String> appliedTo;
    private List<Message> messages;

    public User(String username, String GithubAccountLink, String country, String info, Map<String, Boolean> languagesKnows, List<String> projects, List<String> appliedTo, List<Message> messages)
    {
        this.username = username;
        this.GithubAccountLink = GithubAccountLink;
        this.country = country;
        this.info = info;
        this.languagesKnows = languagesKnows;
        this.projects = projects;
        this.appliedTo = appliedTo;
        this.messages = messages;
    }

    public User(){}

    public String getUsername()
    {
        return username;
    }

    public void setLanguagesKnows(Map<String, Boolean> languagesKnows)
    {
        this.languagesKnows = languagesKnows;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getGithubAccountLink()
    {
        return GithubAccountLink;
    }

    public List<String> getProjects()
    {
        return projects;
    }

    public List<String> getAppliedTo()
    {
        return appliedTo;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public void setProjects(List<String> projects)
    {
        this.projects = projects;
    }

    public void setGithubAccountLink(String githubAccountLink)
    {
        GithubAccountLink = githubAccountLink;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public Map<String, Boolean> getLanguagesKnows()
    {
        return languagesKnows;
    }

    public void addLanguage(String language)
    {
        languagesKnows.put(language, true);
    }

    public void deleteLanguage(String language)
    {
        languagesKnows.remove(language);
    }

    public void addProject(String project)
    {
        projects.add(project);
    }

    public void addProjectAppliedTo(String project)
    {
        appliedTo.add(project);
    }

    public void deleteProjectAppliedTo(String project)
    {
        appliedTo.remove(project);
    }

    public void deleteProject(String project)
    {
        projects.remove(project);
    }

    public void sendMessage(Message message)
    {
        messages.add(message);
    }

    public void deleteMessage(Message message)
    {
        messages.remove(message);
    }


}