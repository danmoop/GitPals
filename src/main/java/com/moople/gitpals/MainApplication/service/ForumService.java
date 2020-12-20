package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.ForumPost;
import com.moople.gitpals.MainApplication.repository.ForumRepository;
import com.moople.gitpals.MainApplication.service.interfaces.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumService implements ForumInterface {

    @Autowired
    private ForumRepository forumRepository;

    @Override
    public List<ForumPost> findAll() {
        return forumRepository.findAll();
    }

    @Override
    public List<ForumPost> findByAuthor(String author) {
        return forumRepository.findByAuthor(author);
    }

    @Override
    public List<ForumPost> matchForumPostsByTitle(String title) {
        return forumRepository.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public ForumPost findByKey(String key) {
        return forumRepository.findByKey(key);
    }

    @Override
    public void save(ForumPost forumPost) {
        forumRepository.save(forumPost);
    }

    @Override
    public void delete(ForumPost forumPost) {
        forumRepository.delete(forumPost);
    }
}

