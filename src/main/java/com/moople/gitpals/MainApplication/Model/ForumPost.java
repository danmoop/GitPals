package com.moople.gitpals.MainApplication.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
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
        String possible = "1234567890asdfghjklmnbvcxz";

        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            stringBuilder.append(possible.charAt(random.nextInt(possible.length())));
        }

        return stringBuilder.toString();
    }
}