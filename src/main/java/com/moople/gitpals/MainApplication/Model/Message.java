package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Message {

    private String author;
    private String content;
    private String timeStamp;
    private TYPE type;

    public Message(String author, String content, TYPE type) {
        this.author = author;
        this.content = content;
        this.type = type;

        this.timeStamp = new Date().toString();
    }

    public enum TYPE {
        BUG_REPORT, PROJECT_INVITATION, INBOX_MESSAGE
    }
}