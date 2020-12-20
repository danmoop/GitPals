package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.ForumPost;

import java.util.List;

public interface ForumInterface {
    List<ForumPost> findAll();

    List<ForumPost> findByAuthor(String author);

    List<ForumPost> matchForumPostsByTitle(String title);

    ForumPost findByKey(String key);

    void save(ForumPost forumPost);

    void delete(ForumPost forumPost);
}
