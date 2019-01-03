package com.moople.gitpals.MainApplication.Model;

public class Message
{
    private String author;
    private String content;

    public Message(String author, String content)
    {
        this.author = author;
        this.content = content;
    }

    public Message(){}

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
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
