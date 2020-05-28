package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForumInterface extends MongoRepository<ForumPost, String> {
    List<ForumPost> findAll();

    List<ForumPost> findByAuthor(String author);

    ForumPost findByKey(String key);
}