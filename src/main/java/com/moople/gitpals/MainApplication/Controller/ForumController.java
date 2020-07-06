package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Comment;
import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ForumController {

    @Autowired
    private ForumInterface forumInterface;

    @Autowired
    private UserService userService;

    /**
     * This request is handled when user wants to open forum page
     *
     * @return forum page
     */
    @GetMapping("/forum")
    public String forumPage(Principal user, Model model) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<ForumPost> posts = forumInterface.findAll();

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);

        return "sections/forum/forum";
    }

    /**
     * This request is handled when user wants to open a posts's page
     *
     * @param key is a post's key which is taken from an address field
     * @return post page
     */
    @GetMapping("/forum/post/{key}")
    public String getForumPost(@PathVariable("key") String key, Principal user, Model model) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = forumInterface.findByKey(key);

        if (post == null) {
            return "redirect:/forum";
        }

        if (user != null && !post.getViewSet().contains(user.getName())) {
            post.getViewSet().add(user.getName());
            forumInterface.save(post);
        }

        try {
            model.addAttribute("userDB", userService.findByUsername(user.getName()));
        } catch (NullPointerException e) {
            model.addAttribute("userDB", null);
        }

        model.addAttribute("post", post);

        return "sections/forum/viewForumPost";
    }

    /**
     * This request is handled when user submits their forum post and it is added to forum
     *
     * @param user    is assigned automatically using spring
     * @param content is taken from html input, it is post's description
     * @return forum post page
     */
    @PostMapping("/addForumPost")
    public String addForumPost(Principal user, @RequestParam("title") String title, @RequestParam("content") String content) {
        if (user == null) {
            return "redirect:/";
        } else {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = new ForumPost(user.getName(), title, content);
        forumInterface.save(post);

        return "redirect:/forum/post/" + post.getKey();
    }

    /**
     * This request is handled when user sends their comments to a forum post
     * A comment will be added and changed will be saved to database
     *
     * @param user        is a user session, assigned automatically
     * @param commentText is a comment text, taken from html input field
     * @param postKey     is a forum post's key, taken from a hidden html input field, assigned by thymeleaf
     * @return forum post page
     */
    @PostMapping("/addCommentToPost")
    public String addCommentToPost(Principal user, @RequestParam("commentText") String commentText, @RequestParam("postKey") String postKey) {
        if (user == null) {
            return "redirect:/";
        } else {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = forumInterface.findByKey(postKey);

        post.getComments().add(new Comment(user.getName(), commentText));
        forumInterface.save(post);

        return "redirect:/forum/post/" + postKey;
    }

    /**
     * This function removed a forum post from the database by key
     *
     * @param user is a forum post's author
     * @param key  is a forum post's key, it is used to find a post among many others
     * @return home page
     */
    @PostMapping("/deleteForumPost")
    public String deleteForumPost(Principal user, @RequestParam("key") String key) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = forumInterface.findByKey(key);

        if (user != null && post != null && user.getName().equals(post.getAuthor())) {
            forumInterface.delete(post);
        }

        return "redirect:/forum";
    }

    /**
     * This function deleted comment added on forum post
     *
     * @param user is comment's author
     * @param key  is a forum post's key
     * @param text is a comment's text
     * @param ts   is a comment's timestamp (when comment was added)
     * @return forum post's page
     */
    @PostMapping("/deleteForumPostComment")
    public String deleteForumPostComment(Principal user, @RequestParam("key") String key, @RequestParam("text") String text, @RequestParam("ts") String ts) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = forumInterface.findByKey(key);

        if (post == null || user == null) {
            return "redirect:/";
        }

        Optional<Comment> comment = post.getComments()
                .stream()
                .filter(postComment -> postComment.getAuthor().equals(user.getName()) && postComment.getText().equals(text) && postComment.getTimeStamp().equals(ts))
                .findFirst();

        if (comment.isPresent() && comment.get().getAuthor().equals(user.getName())) {
            post.getComments().remove(comment.get());
            forumInterface.save(post);
        } else {
            return "redirect:/";
        }

        return "redirect:/forum/post/" + key;
    }

    /**
     * This function edits a comment in a forum post (changes comment's context & marks it as edited)
     *
     * @param user       is an author's authentication
     * @param postKey    is forum post's key required to find a forum post in the database
     * @param text       is a new text that will be set to a comment
     * @param commentKey is a comment key required to find a comment in a list of comments added to a post
     * @return forum post page with edited comment contents
     */
    @PostMapping("/editForumPostComment")
    public String editComment(Principal user, @RequestParam("forumPostKey") String postKey, @RequestParam("editedText") String text, @RequestParam("commentKey") String commentKey) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        ForumPost post = forumInterface.findByKey(postKey);

        if (user == null || post == null) {
            return "redirect:/";
        }

        post.getComments().forEach(comment -> {
            if (comment.getKey().equals(commentKey) && comment.getAuthor().equals(user.getName())) {
                comment.setText(text);
                comment.setEdited(true);
                forumInterface.save(post);
            }
        });

        return "redirect:/forum/post/" + postKey;
    }
}