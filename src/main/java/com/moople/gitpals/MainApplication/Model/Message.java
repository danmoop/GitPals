package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@ToString
public class Message {

    private String author;
    private String recipient;
    private String content;
    private String timeStamp;
    private String type;

    public Message(String author, String recipient, String content, TYPE type) {
        this.author = author;
        this.recipient = recipient;
        this.content = content;
        this.type = type.toString();

        this.timeStamp = new Date().toString();
    }

    public enum TYPE {
        BUG_REPORT, PROJECT_INVITATION, REGULAR_MESSAGE
    }
}