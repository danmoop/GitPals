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

    /**
     * This function returns all forum posts posted to forum
     *
     * @return all forum posts
     */
    @Override
    public List<ForumPost> findAll() {
        return forumRepository.findAll();
    }

    /**
     * This function returns all the posts from a particular user
     *
     * @return all the posts from the user specified
     */
    @Override
    public List<ForumPost> findByAuthor(String author) {
        return forumRepository.findByAuthor(author);
    }

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @return list of posts, whose titles match the target value sent by the user
     */
    @Override
    public List<ForumPost> matchForumPostsByTitle(String title) {
        return forumRepository.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a forum post object obtained by its key
     *
     * @param key is a unique forum post's key
     * @return a forum post object
     */
    @Override
    public ForumPost findByKey(String key) {
        return forumRepository.findByKey(key);
    }

    /**
     * This request is handled when user sends their comments to a forum post
     * A comment will be added and changes will be saved to database
     *
     * @param post     is a forum post object
     * @param username is a username of a user who sends a comment
     * @param comment  is a comment object
     */
    @Override
    public void addComment(ForumPost post, String username, Comment comment) {
        User postAuthor = userService.findByUsername(post.getAuthor());

        post.getComments().add(comment);
        forumRepository.save(post);

        if (!username.equals(postAuthor.getUsername())) {
            Notification notification = new Notification(username + " has left a comment on your forum post (" + post.getTitle() + ") -- " + comment.getText());
            postAuthor.getNotifications().setKey(postAuthor.getNotifications().getKey() + 1);
            postAuthor.getNotifications().getValue().put(notification.getKey(), notification);

            userService.save(postAuthor);
        }
    }

    /**
     * This function deletes a comment added on forum post
     *
     * @param post       is a forum post object
     * @param username   is a username of a user who sends a comment
     * @param commentKey is a comment key
     * @return if comment is present and user who sent the comment is its author
     */
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

    /**
     * This function edits a comment in a forum post (changes comment's context & marks it as edited)
     *
     * @param post        is a forum post object
     * @param username    is a username of a user who sends a comment
     * @param commentKey  is a comment key
     * @param commentText is a comment text
     */
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

    /**
     * This function saves a forum post to the database
     *
     * @param forumPost is a forum post object
     */
    @Override
    public void save(ForumPost forumPost) {
        forumRepository.save(forumPost);
    }

    /**
     * This function deletes a forum post from the database
     *
     * @param forumPost is a forum post object
     */
    @Override
    public void delete(ForumPost forumPost) {
        forumRepository.delete(forumPost);
    }
}