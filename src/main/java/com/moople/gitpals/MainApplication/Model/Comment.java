package com.moople.gitpals.MainApplication.Model;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private String author;
    private String text;
    private String timeStamp;

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        this.timeStamp = new Date().toString();
    }
}