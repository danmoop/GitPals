package com.moople.gitpals.MainApplication.Controller.API;

import com.moople.gitpals.MainApplication.Configuration.JWTUtil;
import com.moople.gitpals.MainApplication.Model.Comment;
import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Response;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forum")
public class ForumAPIController {

    @Autowired
    private ForumInterface forumInterface;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * @return all forum posts fetched from the database
     */
    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumPost> getAll() {
	return forumInterface.findAll();
    }

    /**
     * This function returns a forum post object obtained by its key
     *
     * @param key is a unique forum post's key
     * @return a forum post object
     */
    @GetMapping(value = "/getForumPostById/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ForumPost getForumPostById(@PathVariable String key) {
        return forumInterface.findByKey(key);
    }

    /**
     * This request is handled when user submits their forum post and it is added to forum
     *
     * @param data is information sent by a user (contains post's title & description)
     * @return response if post has been added successfully
     */
    @PostMapping(value = "/addForumPost", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response addForumPost(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String title = data.get("title");
        String description = data.get("description");
        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        ForumPost post = new ForumPost(user.getUsername(), title, description);
        forumInterface.save(post);

        return Response.OK;
    }

    /**
     * This request is handled when user sends their comments to a forum post
     * A comment will be added and changes will be saved to database
     *
     * @param data is information sent by a user (contains comment text and post's key so server could find it)
     * @return response if comment has been added successfully
     */
    @PostMapping(value = "/addCommentToPost", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response addCommentToPost(@RequestBody Map<String, String> data) {
        String jwt = data.get("jwt");
        String commentText = data.get("commentText");
        String postKey = data.get("postKey");

        User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
        ForumPost post = forumInterface.findByKey(postKey);

        if (user == null) {
            return Response.FAILED;
        }

        if (user.isBanned()) {
            return Response.YOU_ARE_BANNED;
        }

        post.getComments().add(new Comment(user.getUsername(), commentText));
        forumInterface.save(post);

        return Response.OK;
    }

    /**
     * This function removes the forum post from the forum
     *
     * @param data contains information sent by the user (contains forum post key & jwt)
     * @return response if post has been deleted successfully
     */
    @PostMapping(value = "/deleteForumPost", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteForumPost(@RequestBody Map<String, String> data) {
	String jwt = data.get("jwt");
	String postKey = data.get("postKey");

	User user = userService.findByUsername(jwtUtil.extractUsername(jwt));
	Post post = forumInterface.findByKey(postKey);

	if (user == null || post == null) {
	    return Response.FAILED;
	}

	if (user.isBanned()) {
	    return Response.YOU_ARE_BANNED;
	}

	if (user.getUsername().equals(post.getAuthor()) {
	    forumInterface.delete(post);

	    return Response.OK;
	}

	return Response.FAILED;
    }

    //TODO: deleteForumPostComment
    //TODO: editForumPostComment
}
