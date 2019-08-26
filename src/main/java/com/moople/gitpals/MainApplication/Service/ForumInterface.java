package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ForumInterface extends MongoRepository<ForumPost, String> {
    List<ForumPost> findAll();
    List<ForumPost> findByAuthor();
}