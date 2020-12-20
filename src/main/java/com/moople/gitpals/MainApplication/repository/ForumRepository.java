package com.moople.gitpals.MainApplication.repository;

import com.moople.gitpals.MainApplication.model.ForumPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForumRepository extends MongoRepository<ForumPost, String> {
    List<ForumPost> findAll();

    List<ForumPost> findByAuthor(String author);

    ForumPost findByKey(String key);
}