package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private String author;
    private String text;
    private String timeStamp;
    private String key;
    private boolean edited;

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        this.timeStamp = new Date().toString();
        this.edited = false;
        this.key = generateKey();
    }

    private String generateKey() {
        return Encrypt.MD5(new Date().getTime() + author + text + timeStamp + Math.random());
    }
}