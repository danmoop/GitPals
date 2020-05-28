package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Data
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

    /**
     * @param viewSet is a set of users who opened a post, like amount of views
     **/
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
        return Encrypt.MD5(new Date().getTime() + author + timeStamp + Math.random());
    }
}