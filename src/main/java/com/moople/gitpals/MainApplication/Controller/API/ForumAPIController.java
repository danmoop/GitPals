package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forum")
public class ForumAPIController {

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This function returns a forum post object obtained by its key
     *
     * @param key is a unique forum post's key
     * @return a forum post object
     */
    @GetMapping("/getForumPostById/{key}")
    public ForumPost getForumPostById(@PathVariable String key) {
        return forumInterface.findByKey(key);
    }

    //TODO: addForumPost
    //TODO: addCommentToPost
    //TODO: deleteForumPost
    //TODO: deleteForumPostComment
    //TODO: editForumPostComment
}