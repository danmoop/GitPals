package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectService;

    @Autowired
    private JavaMailSender mailSender;

    private SimpleMailMessage mailMessage = new SimpleMailMessage();

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
    public String getUserInfo(@RequestParam("userName") String username, Principal admin, Model model) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        User user = userService.findByUsername(username);

        if (user != null) {
            model.addAttribute("user", user.toString());
        } else {
            model.addAttribute("user", username + " is not registered");
        }

        return "sections/users/admin";
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
     * This function will send mail to every user in the system
     *
     * @param admin   is an admin principal object
     * @param subject is a mail letter subject
     * @param text    is a mail letter content
     * @return admin page
     */
    @PostMapping("/sendMailToEveryone")
    public String sendMailToEveryone(
            Principal admin,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text
    ) {
        if (admin == null || !admin.getName().equals("danmoop")) {
            return "redirect:/";
        }

        for (User user : userService.findAll()) {
            if (user.isNotificationsEnabled()) {
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject(subject);
                mailMessage.setText(text);
                mailSender.send(mailMessage);
            }
        }

        return "redirect:/admin";
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

            if (user.isNotificationsEnabled()) {
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject("You got a message on GitPals");
                mailMessage.setText("A message from " + admin.getName() + ": " + message.getContent());
                mailSender.send(mailMessage);
            }

            userService.save(user);
        }

        return "redirect:/admin";
    }
}