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

    @Override
    public Project findByTitle(String title) {
        return projectRepository.findByTitle(title);
    }

    @Override
    public Project findById(String id) {
        return projectRepository.findById(id).orElse(Data.EMPTY_PROJECT);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getFixedNumberOfProjects(int amount) {
        return projectRepository.findAll()
                .stream()
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> matchProjectsByProjectTitle(String title) {
        return projectRepository.findAll().stream()
                .filter(project -> project.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> matchProjectsByTechnologies(List<String> technologies) {
        return projectRepository.findAll().stream()
                .filter(project -> technologies
                        .stream()
                        .anyMatch(tech -> project.getTechnologies().contains(tech.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> matchProjectsByRoles(List<String> roles) {
        return projectRepository.findAll()
                .stream()
                .filter(project -> roles.stream()
                        .anyMatch(role -> project.getRequiredRoles().contains(role.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public void editProjectInfo(Project project, String title, String description, String repoLink, Set<String> technologies, Set<String> roles) {
        project.setTitle(project.getTitle());
        project.setDescription(project.getDescription());
        project.setGithubProjectLink(project.getGithubProjectLink());
        project.setTechnologies(project.getTechnologies());
        project.setRequiredRoles(project.getRequiredRoles());

        save(project);
    }

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

    @Override
    public void save(Project project) {
        projectRepository.save(project);
    }

    @Override
    public void delete(Project project) {
        projectRepository.delete(project);
    }

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