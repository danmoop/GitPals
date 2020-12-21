package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.Comment;
import com.moople.gitpals.MainApplication.model.Notification;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.repository.ProjectRepository;
import com.moople.gitpals.MainApplication.service.interfaces.ProjectInterface;
import com.moople.gitpals.MainApplication.tools.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService implements ProjectInterface {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    /**
     * @return list of all projects created from the database
     */
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    /**
     * This function returns an object fetched from the database by its title
     *
     * @param title is a project title we pass in path
     * @return project json object
     */
    @Override
    public Project findByTitle(String title) {
        return projectRepository.findByTitle(title);
    }

    /**
     * This function returns an object fetched from the database by its unique id
     *
     * @param id is project's unique id number, which we use to find it in the database
     * @return project json object or empty project if such id is not found
     */
    @Override
    public Project findById(String id) {
        return projectRepository.findById(id).orElse(Data.EMPTY_PROJECT);
    }

    /**
     * @param amount is an amount of projects we want to get from the huge list
     * @return list of project which length == amount, so we get fixed list
     */
    @Override
    public List<Project> getFixedNumberOfProjects(int amount) {
        return projectRepository.findAll()
                .stream()
                .limit(amount)
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of project which title matches the input
     *
     * @param title is a project name we pass in path
     * @return list of projects whose title match the one we pass
     */
    @Override
    public List<Project> matchProjectsByProjectTitle(String title) {
        return projectRepository.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of projects with technologies specified by a user
     *
     * @param technologies is a list of technologies project should contain
     * @return list of projects whose technologies match the user's input
     */
    @Override
    public List<Project> matchProjectsByTechnologies(List<String> technologies) {
        return projectRepository.findAll().stream()
                .filter(project -> technologies
                        .stream()
                        .anyMatch(tech -> project.getTechnologies().contains(tech.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * This function returns a list of projects with roles specified by a user
     *
     * @param roles is a list of required roles project should contain
     * @return list of projects whose required roles match the user's input
     */
    @Override
    public List<Project> matchProjectsByRoles(List<String> roles) {
        return projectRepository.findAll()
                .stream()
                .filter(project -> roles.stream()
                        .anyMatch(role -> project.getRequiredRoles().contains(role.toLowerCase())))
                .collect(Collectors.toList());
    }


    /**
     * This function edits information for a chosen project (like change description, roles, etc.)
     *
     * @param technologies is a new list of project's technologies
     * @param roles        is a new list of project's roles
     * @param newTitle     is a new title the user prompts (which can be the same as the old one)
     * @param description  is a new description
     */
    @Override
    public void editProjectInfo(Project project, String newTitle, String description, String repoLink, Set<String> technologies, Set<String> roles) {
        project.setTitle(newTitle);
        project.setDescription(description);
        project.setGithubProjectLink(repoLink);
        project.setTechnologies(technologies);
        project.setRequiredRoles(roles);

        save(project);
    }

    /**
     * This request is handled when user submits their comment
     * It will be added to comments list and saved
     *
     * @param project is a project, which will have a comment added
     * @param comment is a comment object, which will be added to a project
     * @param user    is a user, who sends a comment to a project
     */
    @Override
    public void sendComment(Project project, Comment comment, User user) {
        if (!comment.getAuthor().equals(project.getAuthorName())) {

            // Let the project author know someone has left a comment in a comment section for their project
            User projectAuthor = userService.findByUsername(project.getAuthorName());
            Notification notification = new Notification(user.getUsername() + " has left a comment on your project " + project.getTitle() + ": " + comment.getText());

            projectAuthor.getNotifications().getValue().put(notification.getKey(), notification);
            projectAuthor.getNotifications().setKey(projectAuthor.getNotifications().getKey() + 1);
            userService.save(projectAuthor);
        }

        project.getComments().add(comment);
        save(project);
    }

    /**
     * This function edits a comment in a project (changes comment's context & marks it as edited)
     *
     * @param project    is a project, which will have a comment added
     * @param text       is a new comment text
     * @param commentKey is a comment key, so we could find the comment among others
     * @param username   is a username of a user, who edits the comment
     */
    @Override
    public void editComment(Project project, String text, String commentKey, String username) {
        project.getComments().forEach(comment -> {
            if (comment.getKey().equals(commentKey) && comment.getAuthor().equals(username)) {
                comment.setText(text);
                comment.setEdited(true);
                save(project);
            }
        });
    }

    /**
     * This request is handled when user wants to delete project
     * It will be deleted and applied users will be notified about that
     *
     * @param project is a project, which will be removed
     * @param user    is a user, who wants to remove a project
     */
    @Override
    public void deleteProject(Project project, User user) {
        delete(project);

        if (user.getSubmittedProjects().contains(project.getTitle())) {
            user.getSubmittedProjects().remove(project.getTitle());

            userService.save(user);
        }

        // Remove project from everyone who applied to this project
        // First we stream, it returns list of Strings, map them to User object
        List<User> allUsers = project.getAppliedUsers().stream()
                .map(submittedUser -> userService.findByUsername(submittedUser))
                .collect(Collectors.toList());

        for (User _user : allUsers) {
            Notification notification = new Notification("A project " + project.getTitle() + " you were applied to has been deleted by the project author");
            _user.getProjectsAppliedTo().remove(project.getTitle());
            _user.getNotifications().setKey(_user.getNotifications().getKey() + 1);
            _user.getNotifications().getValue().put(notification.getKey(), notification);

            userService.save(_user);
        }
    }

    /**
     * This functions saves a project to the database
     *
     * @param project is a project, which will be added
     */
    @Override
    public void save(Project project) {
        projectRepository.save(project);
    }

    /**
     * This functions deletes a project from the database
     *
     * @param project is a project, which will be added
     */
    @Override
    public void delete(Project project) {
        projectRepository.delete(project);
    }

    /**
     * This function is handled when user wants to remove their comment
     *
     * @param project     is a project, which will have a comment added
     * @param username    is a username of a user, who edits the comment
     * @param commentText is a comment text
     * @return true if comment is present and user is the author of a comment
     */
    @Override
    public boolean removeComment(Project project, String username, String commentText) {
        Optional<Comment> comment = project.getComments()
                .stream()
                .filter(projectComment -> projectComment.getAuthor().equals(username) && projectComment.getText().equals(commentText))
                .findFirst();

        if (comment.isPresent() && comment.get().getAuthor().equals(username)) {
            project.getComments().remove(comment.get());
            save(project);

            return true;
        }

        return false;
    }

    /**
     * This function lets user either become applied to a project, or un-applied, if they were applied earlier
     *
     * @param project is a project, which application of a user will be changed
     * @param user    is a user, who wants to change their application to a project
     */
    @Override
    public void changeApplicationToAProject(Project project, User user) {

        // Users that already submitted can't submit another time, only once per project
        if (!project.getAppliedUsers().contains(user.getUsername())) {
            project.getAppliedUsers().add(user.getUsername());
            user.getProjectsAppliedTo().add(project.getTitle());

            User projectAuthor = userService.findByUsername(project.getAuthorName());
            Notification notification = new Notification(user.getUsername() + " applied to your project " + project.getTitle());

            projectAuthor.getNotifications().getValue().put(notification.getKey(), notification);
            projectAuthor.getNotifications().setKey(projectAuthor.getNotifications().getKey() + 1);
            userService.save(projectAuthor);
        } else {
            project.getAppliedUsers().remove(user.getUsername());
            user.getProjectsAppliedTo().remove(project.getTitle());
        }

        save(project);
        userService.save(user);
    }
}