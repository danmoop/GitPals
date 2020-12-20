package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.Comment;
import com.moople.gitpals.MainApplication.model.ForumPost;
import com.moople.gitpals.MainApplication.model.Notification;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.repository.ForumRepository;
import com.moople.gitpals.MainApplication.service.interfaces.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumService implements ForumInterface {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserService userService;

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
    public void addComment(ForumPost post, String username, String commentText) {
        User postAuthor = userService.findByUsername(post.getAuthor());

        post.getComments().add(new Comment(username, commentText));
        forumRepository.save(post);

        if (!username.equals(postAuthor.getUsername())) {
            Notification notification = new Notification(username + " has left a comment on your forum post (" + post.getTitle() + ") -- " + commentText);
            postAuthor.getNotifications().setKey(postAuthor.getNotifications().getKey() + 1);
            postAuthor.getNotifications().getValue().put(notification.getKey(), notification);

            userService.save(postAuthor);
        }
    }

    @Override
    public boolean deleteComment(ForumPost post, String username, String commentKey) {
        Optional<Comment> optionalComment = post.getComments().stream().filter(comm -> comm.getKey().equals(commentKey)).findFirst();

        if (optionalComment.isPresent() && post.getAuthor().equals(username)) {
            post.getComments().remove(optionalComment.get());
            save(post);

            return true;
        }

        return false;
    }

    @Override
    public void editComment(ForumPost post, String username, String commentKey, String commentText) {
        post.getComments().forEach(comment -> {
            if (comment.getKey().equals(commentKey) && comment.getAuthor().equals(username)) {
                comment.setText(commentText);
                comment.setEdited(true);
                save(post);
            }
        });
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

