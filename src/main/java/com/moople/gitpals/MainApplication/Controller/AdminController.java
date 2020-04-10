package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectService;

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This function returns admin page if you are an admin
     *
     * @param admin is a current admin authentication
     * @return admin page if username matches and authenticated
     */
    @GetMapping("/admin")
    public String adminPage(Principal admin) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        return "sections/users/admin";
    }

    /**
     * This function return a list of all users registered
     *
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @return list of all users registered
     */
    @PostMapping("/getAllUsers")
    public String getAllUsers(Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        model.addAttribute("users", userService.findAll());

        return "sections/users/admin";
    }

    /**
     * This function returns an information some user
     *
     * @param admin    is a current admin authentication
     * @param model    is where we store the data and send it to html page
     * @param username is a user who we want to get information about
     * @return information about this user
     */
    @PostMapping("/getUserInfo")
    public String getUserInfo(@RequestParam("userName") String username, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user != null) {
            attributes.addFlashAttribute("user", user.toString());
        } else {
            attributes.addFlashAttribute("user", username + " is not registered");
        }

        return "redirect:/admin";
    }

    /**
     * This function returns a list of all added projects
     *
     * @param admin is a current admin authentication
     * @param model is where we store the data and send it to html page
     * @return all the projects added
     */
    @PostMapping("/getAllProjects")
    public String getAllProjects(Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        model.addAttribute("projects", projectService.findAll());

        return "sections/users/admin";
    }

    /**
     * This function returns an information about some project
     *
     * @param admin       is a current admin authentication
     * @param model       is where we store the data and send it to html page
     * @param projectName is a project we want to get information about
     * @return information about the project
     */
    @PostMapping("/getProjectInfo")
    public String getProjectInfo(@RequestParam("projectName") String projectName, Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        Project project = projectService.findByTitle(projectName);

        if (project == null) {
            model.addAttribute("project", projectName + " is not found");
        } else {
            model.addAttribute("project", project.toString());
        }

        return "sections/users/admin";
    }

    /**
     * This function sends a message to everyone registered in the system
     *
     * @param admin is an admin principal object
     * @param text  is a content of the message
     * @return admin page
     * @see Message
     */
    @PostMapping("/sendMessageToEveryone")
    public String sendMessage(Principal admin, @RequestParam("text") String text) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        Message message = new Message(admin.getName(), text, Message.TYPE.INBOX_MESSAGE);

        for (User user : userService.findAll()) {
            user.getMessages().add(message);

            userService.save(user);
        }

        return "redirect:/admin";
    }

    /**
     * This function removes all projects created by a user
     * A quick way to clear projects if some users spams a lot
     *
     * @param admin              is an admin authentication
     * @param username           is a user whose projects will be deleted
     * @param redirectAttributes is where I put a message if user is not found
     * @return admin page
     */
    @PostMapping("/clearAllUserProjects")
    public String clearAllUserProjects(Principal admin, @RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("userProjectsDeletedMessage", "No such user");
            return "redirect:/admin";
        }

        user.getProjects().stream()
                .map(project -> projectService.findByTitle(project))
                .forEach(project -> {
                    notifyAppliedUsers(project);
                    projectService.delete(project);
                });

        user.getProjects().clear();
        userService.save(user);

        redirectAttributes.addFlashAttribute("userProjectsDeletedMessage", "All projects by " + username + " were deleted");

        return "redirect:/admin";
    }

    /**
     * This function returns information about a forum post by its id
     *
     * @param id         is an id of a forum post
     * @param attributes is where I put a message if user is not found
     * @return admin page with information about the post
     */
    @PostMapping("/getForumPostById")
    public String getForumPostById(@RequestParam("id") String id, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        ForumPost post = forumInterface.findByKey(id);

        if (post == null) {
            attributes.addFlashAttribute("forumPost", "No such post with id " + id);
            return "redirect:/admin";
        } else {
            attributes.addFlashAttribute("forumPost", post.toString());
            return "redirect:/admin";
        }
    }

    /**
     * This function is used to delete a forum post by its id
     *
     * @param id         is a post id
     * @param attributes is where I put information about the status of deletion
     * @return admin page with a status of the operation
     */
    @PostMapping("/deleteForumPostById")
    public String deleteForumPostById(@RequestParam("id") String id, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        ForumPost post = forumInterface.findByKey(id);

        if (post == null) {
            attributes.addFlashAttribute("forumPostDeletionStatus", "No such post");
            return "redirect:/admin";
        } else {
            forumInterface.delete(post);

            attributes.addFlashAttribute("forumPostDeletionStatus", "Success!");
            return "redirect:/admin";
        }
    }

    /**
     * This function removes all the forum posts added by a user
     *
     * @param username   is a user whose posts will be deleted
     * @param admin      is a current authorization user
     * @param attributes is where the status of deletion put so it could be displayed in the admin page
     * @return admin page
     */
    @PostMapping("/deleteAllForumPostsByUser")
    public String deleteForumPosts(@RequestParam("username") String username, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        forumInterface.findAll().stream()
                .filter(post -> post.getAuthor().equals(username))
                .forEach(post -> forumInterface.delete(post));

        attributes.addFlashAttribute("forumPostsDeletionForUser", "Success!");

        return "redirect:/admin";
    }

    /**
     * This function is used to notify all applied users to the project that it has been deleted due to rules violation
     *
     * @param project is a project that is being deleted
     */
    private void notifyAppliedUsers(Project project) {
        Message msg = new Message("Project " + project.getTitle() + " you were applied to has been deleted because author has violated the rules on GitPals platform", project.getAuthorName(), Message.TYPE.INBOX_MESSAGE);
        project.getUsersSubmitted().stream()
                .map(username -> userService.findByUsername(username))
                .forEach(user -> {
                    user.getMessages().add(msg);
                    userService.save(user);
                });
    }
}