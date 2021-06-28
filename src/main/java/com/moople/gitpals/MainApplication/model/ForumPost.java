package com.moople.gitpals.MainApplication.model;

import com.moople.gitpals.MainApplication.tools.Encrypt;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Data
@ToString
@NoArgsConstructor
@Document(collection = "forumPosts")
public class ForumPost {

    @Id
    private String id;

    private String author;
    private String title;
    private String content;
    private String key;
    private String timeStamp;
    private Map<String, Comment> comments;
    private HashSet<String> viewSet;

    public ForumPost(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.key = generateKey();
        this.timeStamp = new Date().toString();

        this.viewSet = new HashSet<>();
        this.comments = new HashMap<>();
    }

    private String generateKey() {
        return Encrypt.MD5(author + content + timeStamp + Math.random());
    }
}