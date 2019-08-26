package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ForumPost {
    private int views;
    private String author;
    private String content;
    private List<Comment> comments;

    public ForumPost(String author, String content) {
        this.author = author;
        this.content = content;

        this.views = 0;
        this.comments = new ArrayList<>();
    }
}