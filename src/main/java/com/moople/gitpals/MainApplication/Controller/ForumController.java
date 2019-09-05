package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Comment;
import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class ForumController {

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This request is handled when user wants to open forum page
     *
     * @return forum page
     */
    @GetMapping("/forum")
    public String forumPage(Principal principal, Model model) {
        List<ForumPost> posts = forumInterface.findAll();
        Collections.reverse(posts);

        model.addAttribute("posts", posts);
        model.addAttribute("user", principal);

        return "sections/forum";
    }

    /**
     * This request is handled when user wants to open page with adding posts to forum
     *
     * @return add form page
     */
    @GetMapping("/addForumPostForm")
    public String addForumPostForm() {
        return "sections/addForumPostForm";
    }


    /**
     * This request is handled when user wants to open a posts's page
     *
     * @param key is a post's key which is taken from an address field
     * @return post page
     */
    @GetMapping("/forum/post/{key}")
    public String getForumPost(@PathVariable("key") String key, Principal principal, Model model) {
        ForumPost post = forumInterface.findByKey(key);

        if(post == null) {
            return "redirect:/forum";
        }

        if (principal != null && !post.getViewSet().contains(principal.getName())) {
            post.getViewSet().add(principal.getName());
            forumInterface.save(post);
        }

        model.addAttribute("post", post);
        model.addAttribute("user", principal);
        return "sections/viewForumPost";
    }

    /**
     * This request is handled when user submits their forum post and it is added to forum
     *
     * @param principal is assigned automatically using spring
     * @param content is taken from html input, it is post's description
     * @return forum post page
     */
    @PostMapping("/addForumPost")
    public String addForumPost(Principal principal, @RequestParam("title") String title, @RequestParam("content") String content) {
        ForumPost post = new ForumPost(principal.getName(), title, content);
        forumInterface.save(post);

        return "redirect:/forum/post/" + post.getKey();
    }

    /**
     * This request is handled when user sends their comments to a forum post
     * A comment will be added and changed will be saved to database
     *
     * @param principal is a user session, assigned automatically
     * @param commentText is a comment text, taken from html input field
     * @param postKey is a forum post's key, taken from a hidden html input field, assigned by thymeleaf
     * @return forum post page
     */
    @PostMapping("/addCommentToPost")
    public String addCommentToPost(Principal principal, @RequestParam("commentText") String commentText, @RequestParam("postKey") String postKey) {

        ForumPost post = forumInterface.findByKey(postKey);

        post.getComments().add(new Comment(principal.getName(), commentText));
        forumInterface.save(post);

        return "redirect:/forum/post/" + postKey;
    }
}