package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Comment;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    /**
     * This request is handled when user wants to see a page where they can create a project
     *
     * @return html page where users can submit their project
     */
    @GetMapping("/submitProject")
    public String projectForm(Principal user, Model model) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            model.addAttribute("UserObject", user);
            model.addAttribute("techs", Data.technologiesMap);
            model.addAttribute("projectObject", new Project());

            return "sections/projects/projectSubmitForm";
        }

        return "redirect:/";
    }

    /**
     * This request is handled when user created their project
     * If there is no project with same name, create and save
     * Otherwise display an error message
     *
     * @param project is taken from html form with all the data (project name, description etc.)
     * @param skills   is a checkbox list of technologies for a project that user selects
     * @return create project and redirect to its page, otherwise show an error messaging about identical project name
     **/
    @PostMapping("/projectSubmitted")
    public String projectSubmitted(
            Principal user,
            @ModelAttribute Project project,
            @RequestParam(name = "role", required = false) List<String> roles,
            @RequestParam(name = "skill", required = false) List<String> skills,
            RedirectAttributes redirectAttributes) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (user == null) {
            return "redirect:/";
        }

        // roles and skills may contain empty text fields, so they are removed
        roles = roles.stream()
                .filter(role -> !role.trim().equals(""))
                .collect(Collectors.toList());

        skills = skills.stream()
                .filter(skill -> !skill.trim().equals(""))
                .collect(Collectors.toList());

        if (skills.size() == 0 || roles.size() == 0) {
            redirectAttributes.addFlashAttribute("warning", "Your project should have all the requirements filled!");
            return "redirect:/submitProject";
        }

        User userDB = userService.findByUsername(user.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        System.out.println(roles);
        Project projectDB = projectInterface.findByTitle(project.getTitle());

        if (projectDB == null) {
            Project userProject = new Project(
                    project.getTitle(),
                    project.getDescription(),
                    project.getGithubProjectLink(),
                    userService.findByUsername(user.getName()).getUsername(),
                    skills,
                    roles
            );

            userDB.getProjects().add(userProject.getTitle());

            userService.save(userDB);
            projectInterface.save(userProject);

            return "redirect:/projects/" + userProject.getTitle();
        } else {
            redirectAttributes.addFlashAttribute("warning", "Project with this name already exists");
            return "redirect:/submitProject";
        }
    }

    /**
     * This request is handled when user wants to see a project's page
     * All the data (project name, applied users, author etc.) will be added & displayed
     *
     * @param projectName is taken from an address field - like "/project/UnrealEngine"
     * @return html project page with it's title, author, description, technologies etc
     **/
    @GetMapping("/projects/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model, Principal user) {

        Project project = projectInterface.findByTitle(projectName);

        if (project == null) {
            return "error/projectDeleted";
        } else {
            model.addAttribute("project", project);

            if (user != null) {
                User userDB = userService.findByUsername(user.getName());

                if (userDB.isBanned()) {
                    return "sections/users/banned";
                }

                model.addAttribute("userDB", userDB);
            }

            return "sections/projects/projectViewPage";
        }
    }

    /**
     * This request is handled when user wants to apply to a project
     *
     * @param link is project's title which is taken from a hidden html text field (value assigned automatically with thymeleaf)
     * @return redirect to the same project page
     **/
    @PostMapping("/applyForProject")
    public String applyForProject(@RequestParam("linkInput") String link, Principal user) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (user == null) {
            return "redirect:/";
        }

        User userForApply = userService.findByUsername(user.getName());

        if (userForApply.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectInterface.findByTitle(link);

        // Users that already submitted can't submit another time, only once per project
        if (!project.getUsersSubmitted().contains(user.getName())) {
            project.getUsersSubmitted().add(userForApply.getUsername());
            userForApply.getProjectsAppliedTo().add(project.getTitle());

            projectInterface.save(project);
            userService.save(userForApply);
        }

        return "redirect:/projects/" + link;
    }

    /**
     * This request is handled when user wants to un-apply from a project
     * They will be removed from applied list
     *
     * @param link is project's title which is taken from a hidden html text field (value assigned automatically with thymeleaf)
     * @return redirect to the same project page
     **/
    @PostMapping("/unapplyForProject")
    public String unapplyForProject(@RequestParam("linkInput") String link, Principal user) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (user == null) {
            return "redirect:/";
        }

        Project projectDB = projectInterface.findByTitle(link);
        User userDB = userService.findByUsername(user.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        if (projectDB.getUsersSubmitted().contains(userDB.getUsername())) {
            projectDB.getUsersSubmitted().remove(userDB.getUsername());
            userDB.getProjectsAppliedTo().remove(projectDB.getTitle());

            projectInterface.save(projectDB);
            userService.save(userDB);
        }

        return "redirect:/projects/" + link;
    }

    /**
     * This request is handled when user wants to delete project
     * It will be deleted and applied users will be notified about that
     *
     * @param projectName is project's title which is taken from a html text field
     * @return redirect to the index page
     **/
    @PostMapping("/deleteProject")
    public String projectDeleted(Principal user, @RequestParam("projectName") String projectName) {

        Project project = projectInterface.findByTitle(projectName);
        User userDB = userService.findByUsername(user.getName());

        // If authenticated user is null (so there is no auth), redirect to main page
        if (userDB == null || project == null) {
            return "redirect:/";
        }

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        // Remove project from author's projects list
        if (userDB.getUsername().equals(project.getAuthorName())) {
            projectInterface.delete(project);

            if (userDB.getProjects().contains(project.getTitle())) {
                userDB.getProjects().remove(project.getTitle());

                userService.save(userDB);
            }

            // Remove project from everyone who applied to this project
            // First we stream, it returns list of Strings, map them to User object
            List<User> allUsers = project.getUsersSubmitted().stream()
                    .map(submittedUser -> userService.findByUsername(submittedUser))
                    .collect(Collectors.toList());

            // Every applied user will receive a message about project deletion
            //Message notification = new Message(project.getAuthorName(), "Project " + projectName + " you were applied to has been deleted", Message.TYPE.REGULAR_MESSAGE);

            for (User _user : allUsers) {
                _user.getProjectsAppliedTo().remove(project.getTitle());
                //_user.getMessages().add(notification);

                userService.save(_user);
            }

            return "redirect:/";

        } else {
            return "error/siteBroken";
        }
    }

    /**
     * This request is handled when user submits their comment
     * It will be added to comments list and saved
     *
     * @param projectName is taken from a hidden html text field
     * @param text        is taken from a html text field
     * @param user        is assigned automatically using thymeleaf
     * @return project comments page with new comment
     */
    @PostMapping("/sendComment")
    public String sendComment(@RequestParam("projectName") String projectName, @RequestParam("text") String text, Principal user, RedirectAttributes redirectAttributes) {

        if (text.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your comment should have any text!");
            return "redirect:/projects/" + projectName;
        }

        Project project = projectInterface.findByTitle(projectName);

        if (user != null && project != null) {

            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Comment comment = new Comment(user.getName(), text);

            project.getComments().add(comment);
            projectInterface.save(project);
        }

        return "redirect:/projects/" + projectName;
    }

    /**
     * This function removes a comment added below project description
     *
     * @param user        is comment's author
     * @param projectName is a project name
     * @param text        is a comment text
     * @param ts          is a comment's timestamp (when comment was added)
     * @return project page
     */
    @PostMapping("/deleteComment")
    public String deleteComment(Principal user, @RequestParam("projectName") String projectName, @RequestParam("text") String text, @RequestParam("ts") String ts) {
        User userDB = userService.findByUsername(user.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectInterface.findByTitle(projectName);

        if (project == null) {
            return "redirect:/";
        }

        Optional<Comment> comment = project.getComments()
                .stream()
                .filter(projectComment -> projectComment.getAuthor().equals(user.getName()) && projectComment.getText().equals(text) && projectComment.getTimeStamp().equals(ts))
                .findFirst();

        if (comment.isPresent() && comment.get().getAuthor().equals(user.getName())) {
            project.getComments().remove(comment.get());
            projectInterface.save(project);
        } else {
            return "redirect:/";
        }

        return "redirect:/projects/" + projectName;
    }


    /**
     * This function edits a comment in a project (changes comment's context & marks it as edited)
     *
     * @param user        is an author's authentication
     * @param projectName is project's name required to find a forum post in the database
     * @param text        is a new text that will be set to a comment
     * @param commentKey  is a comment key required to find a comment in a list of comments added to a post
     * @return project page with edited comment contents
     */
    @PostMapping("/editProjectComment")
    public String editComment(Principal user, @RequestParam("projectName") String projectName, @RequestParam("editedText") String text, @RequestParam("commentKey") String commentKey) {

        Project project = projectInterface.findByTitle(projectName);

        if (user == null || project == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(user.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        project.getComments().forEach(comment -> {
            if (comment.getKey().equals(commentKey) && comment.getAuthor().equals(user.getName())) {
                comment.setText(text);
                comment.setEdited(true);
                projectInterface.save(project);
            }
        });

        return "redirect:/projects/" + projectName;
    }
}