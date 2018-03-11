package com.moople.gitpals.MainApplication.Model;

public class Message
{
    private User author;
    private String content;

    public Message(User author, String content)
    {
        this.author = author;
        this.content = content;
    }

    public Message(){}

    public User getAuthor()
    {
        return author;
    }

    public void setAuthor(User author)
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
