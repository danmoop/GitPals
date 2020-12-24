package com.moople.gitpals.MainApplication.controller;

import com.moople.gitpals.MainApplication.model.ForumPost;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    @Autowired
    private GlobalMessageService globalMessageService;

    @Autowired
    private AdminService adminService;

    /**
     * This function returns admin page if you are an admin
     *
     * @param admin is a current admin authentication
     * @return admin page if username matches and authenticated
     */
    @GetMapping("/admin")
    public String adminPage(Principal admin) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        model.addAttribute("numberOfUsers", userService.findAll().size());

        return "sections/users/admin";
    }

    /**
     * This function returns an information some user
     *
     * @param admin      is a current admin authentication
     * @param attributes is where we store the data and send it to html page
     * @param username   is a user who we want to get information about
     * @return information about this user
     */
    @PostMapping("/getUserInfo")
    public String getUserInfo(@RequestParam("userName") String username, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user != null) {
            user.setDialogs(null);
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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        model.addAttribute("numberOfProjects", projectService.findAll().size());

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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
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
     * This function removes all projects created by a user
     * A quick way to clear projects if some users spams a lot
     *
     * @param admin              is an admin authentication
     * @param username           is a user whose projects will be deleted
     * @param redirectAttributes is where I put a message if user is not found
     * @return admin page
     */
    @PostMapping("/clearAllUserProjects")
    public String clearAllUserProjects(Principal admin, @RequestParam String username, RedirectAttributes redirectAttributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        if (!adminService.removeAllUserProjects(username)) {
            redirectAttributes.addFlashAttribute("userProjectsDeletedMessage", "No such user");
            return "redirect:/admin";
        }

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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        ForumPost post = forumService.findByKey(id);

        if (post == null) {
            attributes.addFlashAttribute("forumPost", "No such post with id " + id);
        } else {
            attributes.addFlashAttribute("forumPost", post.toString());
        }
        return "redirect:/admin";
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
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        ForumPost post = forumService.findByKey(id);

        if (post == null) {
            attributes.addFlashAttribute("forumPostDeletionStatus", "No such post");
        } else {
            forumService.delete(post);

            attributes.addFlashAttribute("forumPostDeletionStatus", "Success!");
        }
        return "redirect:/admin";
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
    public String deleteForumPosts(@RequestParam String username, Principal admin, RedirectAttributes attributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        adminService.deleteAllForumPostsByUser(username);

        attributes.addFlashAttribute("forumPostsDeletionForUser", "Success!");

        return "redirect:/admin";
    }

    /**
     * This function bans user so they can't do anything anymore
     *
     * @param admin              is an admin authentication
     * @param username           is a user who will be banned
     * @param redirectAttributes is where the information about the user is put
     * @return to the admin page and inform if the user is banned now
     */
    @PostMapping("/banUser")
    public String banUser(Principal admin, @RequestParam String username, RedirectAttributes redirectAttributes) {
        if (!userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("banMsg", username + " is not registered");
            return "redirect:/admin";
        }

        user.setBanned(true);
        userService.save(user);

        redirectAttributes.addFlashAttribute("banMsg", username + " has been banned");
        return "redirect:/admin";
    }

    /**
     * This function unbans user so they could access the app
     *
     * @param admin              is an admin authentication
     * @param username           is a user who will be unbanned
     * @param redirectAttributes is where the information about the user is put
     * @return to the admin page and inform if the user is unbanned now
     */
    @PostMapping("/unbanUser")
    public String unbanUser(Principal admin, @RequestParam String username, RedirectAttributes redirectAttributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("unbanMsg", username + " is not registered");
            return "redirect:/admin";
        }

        user.setBanned(false);
        userService.save(user);

        redirectAttributes.addFlashAttribute("unbanMsg", username + " has been unbanned");
        return "redirect:/admin";
    }

    /**
     * This function returns a list of users who were active in the previous 24 hours
     *
     * @param admin              is an admin authentication
     * @param redirectAttributes is where information about users is put
     * @return admin page and display a list of active users
     */
    @PostMapping("/getActiveDailyUsers")
    public String getActiveDailyUsers(Principal admin, RedirectAttributes redirectAttributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        List<String> activeUsers = adminService.getActiveDailyUsers();

        redirectAttributes.addFlashAttribute("activeDailyUsers", activeUsers);
        return "redirect:/admin";
    }

    /**
     * This function returns a list of users who were active in the previous 7 days
     *
     * @param admin              is an admin authentication
     * @param redirectAttributes is where information about users is put
     * @return admin page and display a list of active users
     */
    @PostMapping("/getActiveWeeklyUsers")
    public String getActiveWeeklyUsers(Principal admin, RedirectAttributes redirectAttributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        List<String> activeUsers = adminService.getActiveWeeklyUsers();

        redirectAttributes.addFlashAttribute("activeWeeklyUsers", activeUsers);
        return "redirect:/admin";
    }

    /**
     * This function creates a global alert that will be displayed on GitPals home page
     *
     * @param admin is an admin authentication
     * @param text  is a new text for an alert
     * @return to the admin page
     */
    @PostMapping("/modifyGlobalAlert")
    public String modifyGlobalAlert(Principal admin, @RequestParam String text) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        adminService.modifyGlobalAlert(text);

        return "redirect:/admin";
    }

    /**
     * This function removes a global alert so it won't be displayed anymore
     *
     * @param admin is an admin authentication
     * @return to the admin page
     */
    @PostMapping("/removeGlobalAlert")
    public String removeGlobalAlert(Principal admin) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        globalMessageService.deleteAll();

        return "redirect:/admin";
    }

    /**
     * This function makes some user an admin, or the opposite, the regular user
     *
     * @param admin              is a current admin auth who sends the request
     * @param username           is a user who will become and admin or the opposite
     * @param redirectAttributes is where the message is put if the user is not found in the database
     * @return the admin page with a message, which tells that a user has become either an admin or a user
     */
    @PostMapping("/makeAdmin")
    public String makeAdmin(Principal admin, @RequestParam String username, RedirectAttributes redirectAttributes) {
        if (admin == null || !userService.findByUsername(admin.getName()).isAdmin()) {
            return "redirect:/";
        }

        if (!adminService.makeUserAnAdmin(username)) {
            redirectAttributes.addFlashAttribute("makeAdminMsg", username + " is not registered");
            return "redirect:/admin";
        }

        redirectAttributes.addFlashAttribute("makeAdminMsg", "Done");

        return "redirect:/admin";
    }
}