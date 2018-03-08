package com.moople.gitpals.MainApplication.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User
{
    private String username;
    private String GithubAccountLink;
    private String country;
    private List<String> languagesKnows;

    public User(String username, String GithubAccountLink, String country, List<String> languagesKnows)
    {
        this.username = username;
        this.GithubAccountLink = GithubAccountLink;
        this.country = country;
        this.languagesKnows = languagesKnows;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getGithubAccountLink()
    {
        return GithubAccountLink;
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

    public List<String> getLanguagesKnows()
    {
        return languagesKnows;
    }

    public void addLanguage(String language)
    {
        languagesKnows.add(language);
    }

    public void deleteLanguage(String language)
    {
        languagesKnows.remove(language);
    }
}