package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private String author;
    private String text;
    private Date timeStamp;
    private String key;
    private boolean edited;

    public Comment(String author, String text) {
        this.author = author;
        this.text = text.trim();
        this.timeStamp = new Date();
        this.key = generateKey();
        this.edited = false;
    }

    /**
     * This function generates a unique key in order to find the comment among others
     *
     * @return the key generated using MD5 taking multiple parameters in order to get as unique key as possible
     */
    private String generateKey() {
        return Encrypt.MD5(new Date().getTime() + author + text + timeStamp + Math.random());
    }
}