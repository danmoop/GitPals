package com.moople.gitpals.MainApplication.controller;

import com.moople.gitpals.MainApplication.model.Comment;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.ProjectService;
import com.moople.gitpals.MainApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    /**
     * This request is handled when user wants to see a project's page
     * All the data (project name, applied users, author etc.) will be added & displayed
     *
     * @param projectName is taken from an address field - like "/project/UnrealEngine"
     * @return html project page with it's title, author, description, technologies etc
     **/
    @GetMapping("/projects/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model, Principal auth) {

        Project project = projectService.findByTitle(projectName);

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

        Project projectDB = projectService.findByTitle(project.getTitle());

        if (projectDB == null) {
            Project userProject = new Project(
                    project.getTitle(),
                    project.getDescription(),
                    project.getGithubProjectLink(),
                    userService.findByUsername(auth.getName()).getUsername(),
                    skills,
                    roles
            );

            userDB.getSubmittedProjects().add(userProject.getTitle());

            userService.save(userDB);
            projectService.save(userProject);

            return "redirect:/projects/" + userProject.getTitle();
        } else {
            redirectAttributes.addFlashAttribute("warning", "Project with this name already exists");
            return "redirect:/submitProject";
        }
    }

    /**
     * This request is handled when user wants to apply to a project
     *
     * @param projectTitle is project's title which is taken from a hidden html text field (value assigned automatically with thymeleaf)
     * @return redirect to the same project page
     **/
    @PostMapping("/toggleApplication")
    public String applyForProject(@RequestParam String projectTitle, Principal auth) {

        // If authenticated user is null (so there is no auth), redirect to main page
        if (auth == null) {
            return "redirect:/";
        }

        User applyingUser = userService.findByUsername(auth.getName());

        if (applyingUser.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectService.findByTitle(projectTitle);

        projectService.changeApplicationToAProject(project, applyingUser);

        return "redirect:/projects/" + projectTitle;
    }

    /**
     * This request is handled when user wants to delete project
     * It will be deleted and applied users will be notified about that
     *
     * @param projectName is project's title which is taken from a html text field
     * @return redirect to the index page
     **/
    @PostMapping("/deleteProject")
    public String projectDeleted(Principal auth, @RequestParam String projectName) {

        Project project = projectService.findByTitle(projectName);
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
            projectService.deleteProject(project, userDB);

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

        if (text.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your comment should have any text!");
            return "redirect:/projects/" + projectName;
        }

        Project project = projectService.findByTitle(projectName);

        if (auth != null && project != null) {

            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }

            Comment comment = new Comment(auth.getName(), text);

            projectService.sendComment(project, comment, userDB);
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
    public String editComment(Principal auth, @RequestParam String projectName, @RequestParam String text, @RequestParam String commentKey) {

        Project project = projectService.findByTitle(projectName);

        if (auth == null || project == null) {
            return "redirect:/";
        }

        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        projectService.editComment(project, text, commentKey, auth.getName());

        return "redirect:/projects/" + projectName;
    }

    /**
     * This function removes a comment added below project description
     *
     * @param auth        is comment's author
     * @param projectName is a project name
     * @param key         is a comment key
     * @return project page
     */
    @PostMapping("/deleteComment")
    public String deleteComment(Principal auth, @RequestParam String projectName, @RequestParam String key) {
        User userDB = userService.findByUsername(auth.getName());

        if (userDB.isBanned()) {
            return "sections/users/banned";
        }

        Project project = projectService.findByTitle(projectName);

        if (project == null) {
            return "redirect:/";
        }

        if (projectService.removeComment(project, auth.getName(), key)) {
            return "redirect:/projects/" + projectName;
        }

        return "redirect:/";
    }

    /**
     * This function edits information for a chosen project (like change description, roles, etc.)
     *
     * @param techs        is a new list of project's technologies
     * @param roles        is a new list of project's roles
     * @param newTitle     is a new title the user prompts (which can be the same as the old one)
     * @param currentTitle is an original project title
     * @param description  is a new description
     * @return to the project page
     */
    @PostMapping("/editProjectInfo")
    public String editProjectInfo(
            Principal auth,
            @RequestParam(value = "tech", required = false) Set<String> techs,
            @RequestParam(value = "role", required = false) Set<String> roles,
            @RequestParam String newTitle,
            @RequestParam String currentTitle,
            @RequestParam String repoLink,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        Project project = projectService.findByTitle(currentTitle);

        if (project == null || !project.getAuthorName().equals(auth.getName())) {
            return "redirect:/";
        }

        if (techs == null || roles == null || techs.size() == 0 || roles.size() == 0) {
            redirectAttributes.addFlashAttribute("msg", "Information about technologies or roles can't be empty!");
            return "redirect:/projects/" + currentTitle;
        }

        if (currentTitle.equals(newTitle) || projectService.findByTitle(newTitle) == null) {
            projectService.editProjectInfo(project, newTitle, auth.getName(), description, repoLink, techs, roles);
        } else {
            redirectAttributes.addFlashAttribute("msg", "A project with your new title already exists!");
            return "redirect:/projects/" + currentTitle;
        }

        return "redirect:/projects/" + newTitle;
    }

    /**
     * This function allows to like projects
     *
     * @param principal  is user's authorization
     * @param title      is a project title user wants to like
     * @param attributes is attributes for showing an error after redirecting
     * @return index page
     */
    @PostMapping("/likeProject")
    public String likeProject(Principal principal, @RequestParam String title, @RequestParam int page, RedirectAttributes attributes) {
        if (principal == null) {
            attributes.addFlashAttribute("error", "Please sign it to like this project.");
            return "redirect:/page/" + page;
        }

        User user = userService.findByUsername(principal.getName());
        Project project = projectService.findByTitle(title);

        if (project == null) {
            attributes.addFlashAttribute("error", title + " is not found.");
            return "redirect:/page/" + page;
        }

        if (project.getLikes().contains(user.getUsername())) {
            project.getLikes().remove(user.getUsername());
        } else {
            project.getLikes().add(user.getUsername());
        }

        projectService.save(project);

        return "redirect:/page/" + page;
    }
}