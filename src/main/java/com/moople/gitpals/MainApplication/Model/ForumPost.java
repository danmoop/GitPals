package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "forumPosts")
public class ForumPost {
    private int views;
    private String author;
    private String title;
    private String content;
    private String key;
    private List<Comment> comments;

    public ForumPost(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.key = UUID.randomUUID().toString();

        this.views = 0;
        this.comments = new ArrayList<>();
    }
}