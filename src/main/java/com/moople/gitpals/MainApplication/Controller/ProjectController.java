package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Comment;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
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
import java.util.Set;
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
    public String projectForm(Principal auth, Model model) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            model.addAttribute("UserObject", auth);
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
     * @param skills  is a checkbox list of technologies for a project that user selects
     * @return create project and redirect to its page, otherwise show an error messaging about identical project name
     **/
    @PostMapping("/projectSubmitted")
    public String projectSubmitted(
            Principal auth,
            @ModelAttribute Project project,
            @RequestParam(name = "role", required = false) Set<String> roles,
            @RequestParam(name = "skill", required = false) Set<String> skills,
            RedirectAttributes redirectAttributes) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (auth == null) {
            return "redirect:/";
        }

        // roles and skills may contain empty text fields, so they are removed
        roles = roles.stream()
                .filter(role -> !role.trim().equals(""))
                .collect(Collectors.toSet());

        skills = skills.stream()
                .filter(skill -> !skill.trim().equals(""))
                .collect(Collectors.toSet());

        if (skills.size() == 0 || roles.size() == 0) {
            redirectAttributes.addFlashAttribute("warning", "Your project should have all the requirements filled!");
            return "redirect:/submitProject";
        }

        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        System.out.println(roles);
        Project projectDB = projectInterface.findByTitle(project.getTitle());

        if (projectDB == null) {
            Project userProject = new Project(
                    project.getTitle().trim(),
                    project.getDescription().trim(),
                    project.getGithubProjectLink().trim(),
                    userService.findByUsername(auth.getName()).getUsername(),
                    skills,
                    roles
            );

            userDB.getSubmittedProjects().add(userProject.getTitle());

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
    public String projectPage(@PathVariable String projectName, Model model, Principal auth) {

        Project project = projectInterface.findByTitle(projectName);

        if (project == null) {
            return "error/projectDeleted";
        } else {
            model.addAttribute("project", project);

            if (auth != null) {
                User userDB = userService.findByUsername(auth.getName());

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
    public String applyForProject(@RequestParam("linkInput") String link, Principal auth) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (auth == null) {
            return "redirect:/";
        }

        User userForApply = userService.findByUsername(auth.getName());

        if (userForApply.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectInterface.findByTitle(link);

        // Users that already submitted can't submit another time, only once per project
        if (!project.getAppliedUsers().contains(auth.getName())) {
            project.getAppliedUsers().add(userForApply.getUsername());
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
    public String unapplyForProject(@RequestParam("linkInput") String link, Principal auth) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (auth == null) {
            return "redirect:/";
        }

        Project projectDB = projectInterface.findByTitle(link);
        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        if (projectDB.getAppliedUsers().contains(userDB.getUsername())) {
            projectDB.getAppliedUsers().remove(userDB.getUsername());
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
    public String projectDeleted(Principal auth, @RequestParam("projectName") String projectName) {

        Project project = projectInterface.findByTitle(projectName);
        User userDB = userService.findByUsername(auth.getName());

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

            if (userDB.getSubmittedProjects().contains(project.getTitle())) {
                userDB.getSubmittedProjects().remove(project.getTitle());

                userService.save(userDB);
            }

            // Remove project from everyone who applied to this project
            // First we stream, it returns list of Strings, map them to User object
            List<User> allUsers = project.getAppliedUsers().stream()
                    .map(submittedUser -> userService.findByUsername(submittedUser))
                    .collect(Collectors.toList());

            for (User _user : allUsers) {
                _user.getProjectsAppliedTo().remove(project.getTitle());

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
     * @param auth        is assigned automatically using thymeleaf
     * @return project comments page with new comment
     */
    @PostMapping("/sendComment")
    public String sendComment(@RequestParam String projectName, @RequestParam String text, Principal auth, RedirectAttributes redirectAttributes) {

        if (text.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your comment should have any text!");
            return "redirect:/projects/" + projectName;
        }

        Project project = projectInterface.findByTitle(projectName);

        if (auth != null && project != null) {

            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Comment comment = new Comment(auth.getName(), text);

            project.getComments().add(comment);
            projectInterface.save(project);
        }

        return "redirect:/projects/" + projectName;
    }

    /**
     * This function removes a comment added below project description
     *
     * @param auth        is comment's author
     * @param projectName is a project name
     * @param text        is a comment text
     * @param ts          is a comment's timestamp (when comment was added)
     * @return project page
     */
    @PostMapping("/deleteComment")
    public String deleteComment(Principal auth, @RequestParam("projectName") String projectName, @RequestParam("text") String text, @RequestParam("ts") String ts) {
        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectInterface.findByTitle(projectName);

        if (project == null) {
            return "redirect:/";
        }

        Optional<Comment> comment = project.getComments()
                .stream()
                .filter(projectComment -> projectComment.getAuthor().equals(auth.getName()) && projectComment.getText().equals(text))
                .findFirst();

        if (comment.isPresent() && comment.get().getAuthor().equals(auth.getName())) {
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
     * @param auth        is an author's authentication
     * @param projectName is project's name required to find a forum post in the database
     * @param text        is a new text that will be set to a comment
     * @param commentKey  is a comment key required to find a comment in a list of comments added to a post
     * @return project page with edited comment contents
     */
    @PostMapping("/editProjectComment")
    public String editComment(Principal auth, @RequestParam("projectName") String projectName, @RequestParam("editedText") String text, @RequestParam("commentKey") String commentKey) {

        Project project = projectInterface.findByTitle(projectName);

        if (auth == null || project == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        project.getComments().forEach(comment -> {
            if (comment.getKey().equals(commentKey) && comment.getAuthor().equals(auth.getName())) {
                comment.setText(text);
                comment.setEdited(true);
                projectInterface.save(project);
            }
        });

        return "redirect:/projects/" + projectName;
    }

    /**
     * This function edits information for a chosen project (like change description, roles, etc.)
     *
     * @param techs       is a new list of project's technologies
     * @param roles       is a new list of project's roles
     * @param newTitle    is a new title the user prompts (which can be the same as the old one)
     * @param title       is an original project title
     * @param description is a new description
     * @return to the project page
     */
    @PostMapping("/editProjectInfo")
    public String editProjectInfo(
            @RequestParam(value = "tech", required = false) Set<String> techs,
            @RequestParam(value = "role", required = false) Set<String> roles,
            @RequestParam("newTitle") String newTitle,
            @RequestParam("title") String title,
            @RequestParam("repoLink") String repoLink,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {

        if (techs == null || roles == null || techs.size() == 0 || roles.size() == 0) {
            redirectAttributes.addFlashAttribute("msg", "Information about technologies or roles can't be empty!");
            return "redirect:/projects/" + title;
        }

        if (title.equals(newTitle) || projectInterface.findByTitle(newTitle) == null) {
            Project project = projectInterface.findByTitle(title);

            project.setTitle(newTitle);
            project.setGithubProjectLink(repoLink);
            project.setDescription(description);
            project.setTechnologies(techs);
            project.setRequiredRoles(roles);
            projectInterface.save(project);
        } else {
            redirectAttributes.addFlashAttribute("msg", "A project with your new title already exists!");
            return "redirect:/projects/" + title;
        }

        return "redirect:/projects/" + newTitle;
    }
}