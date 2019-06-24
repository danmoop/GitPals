package com.moople.gitpals.MainApplication.Model;

import java.util.Date;

public class Message
{
    private String author;
    private String content;
    private String timeStamp;

    public Message(String author, String content)
    {
        this.author = author;
        this.content = content;

        this.timeStamp = new Date().toString();
    }

    public Message(){}

    public String getTimeStamp()
    {
        return timeStamp;
    }

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
