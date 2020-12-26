package com.moople.gitpals.MainApplication.model;

import com.moople.gitpals.MainApplication.tools.Encrypt;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
    private List<Comment> comments;
    private HashSet<String> viewSet;

    public ForumPost(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.key = generateKey();
        this.timeStamp = new Date().toString();

        this.viewSet = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    private String generateKey() {
        return Encrypt.MD5(author + content + timeStamp + Math.random());
    }
}