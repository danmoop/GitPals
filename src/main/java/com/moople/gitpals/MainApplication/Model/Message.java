package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Message
{
    private String author;
    private String content;
    private String timeStamp;
    private boolean isBugReport;

    public Message(String author, String content)
    {
        this.author = author;
        this.content = content;
        this.isBugReport = false;

        this.timeStamp = new Date().toString();
    }
}