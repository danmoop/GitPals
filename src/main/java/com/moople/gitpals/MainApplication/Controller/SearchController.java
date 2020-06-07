package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.ForumPost;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;
import com.moople.gitpals.MainApplication.Service.Data;
import com.moople.gitpals.MainApplication.Service.ForumInterface;
import com.moople.gitpals.MainApplication.Service.ProjectInterface;
import com.moople.gitpals.MainApplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectInterface projectInterface;

    @Autowired
    private ForumInterface forumInterface;

    /**
     * This request is handled when user wants to see a search page to find a project or a user
     *
     * @return html page where users can find a project or a user by name
     */
    @GetMapping("/search")
    public String searchPage(Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        model.addAttribute("techs", Data.technologiesMap);

        return "sections/searchForm";
    }

    /**
     * This request is handled when user submits a username they want to find
     * List of users will be displayed
     *
     * @param username is taken from a html textfield
     * @return list of users whose nicknames contain user's input
     **/
    @PostMapping("/findUser")
    public String foundUsers(@RequestParam("user_name") String username, Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<String> matchUsers = userService.findAll().stream()
                .filter(matchedUser -> matchedUser.getUsername().toLowerCase().contains(username.toLowerCase()))
                .map(User::getUsername).collect(Collectors.toList());

        model.addAttribute("match_users", matchUsers);

        return "sections/users/matchUsers";
    }

    /**
     * This request is handled when user submits a project name they want to find
     * A list of projects will be displayed
     *
     * @param projectName is taken from a html textfield
     * @return list of projects whose titles contain user's input
     **/
    @PostMapping("/findProject")
    public String foundProjects(@RequestParam("project_name") String projectName, Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<String> matchProjects = projectInterface.findAll()
                .stream()
                .filter(project -> project.getTitle().toLowerCase().contains(projectName.toLowerCase()))
                .map(Project::getTitle)
                .collect(Collectors.toList());

        model.addAttribute("match_projects", matchProjects);

        return "sections/projects/matchProjects";
    }

    /**
     * This request is handled when user wants to find users who know specific skills
     *
     * @param skills is a list of skills required
     * @param model  is assigned automatically, that's where the data goes
     * @return page where all the users are displayed
     */
    @PostMapping("/findUsersBySkills")
    public String usersBySkills(@RequestParam("skills") List<String> skills, Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        Set<String> users = userService.findBySkillList(skills);

        model.addAttribute("match_users", users);

        return "sections/users/matchUsers";
    }

    /**
     * This request is handled when user wants to sort projects by language
     * They will be sorted and displayed
     *
     * @param data     is a list of technologies checkboxes user select manually
     * @param isUnique is a condition whether there are any other techs EXCEPT what users choose (null if checkbox is not selected, "off" if selected)
     * @return a list of projects according to user's preference
     **/
    @PostMapping("/sortProjects")
    public String projectsSorted(@RequestParam("requirement") List<String> data, @RequestParam(required = false, name = "isUnique") boolean isUnique, Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<Project> allProjects = projectInterface.findAll();

        List<String> matchProjects;

        /** @param isUnique does the following:
         * if there is a project with some requirements and we mark a checkbox then
         * it will find a project with chosen requirements ONLY
         *
         * if checkbox is not selected it will find the same project by ONE of the requirements
         */
        if (isUnique) { // true - if checkbox IS selected
            matchProjects = allProjects.stream()
                    .filter(project -> project.getRequirements().equals(data))
                    .map(Project::getTitle)
                    .collect(Collectors.toList());
        } else { // false - checkbox IS NOT selected
            matchProjects = allProjects.stream()
                    .filter(project -> data.stream()
                            .anyMatch(req -> project.getRequirements().contains(req)))
                    .map(Project::getTitle)
                    .collect(Collectors.toList());
        }

        model.addAttribute("match_projects", matchProjects);

        return "sections/projects/matchProjects";
    }

    /**
     * This request finds all the forum posts whose titles match target value
     *
     * @param postName is target value, compare all the posts with it
     * @param model    is where list with posts is put
     * @return list with posts to the result page
     */
    @PostMapping("/findForumPosts")
    public String findForumPosts(@RequestParam("post_name") String postName, Model model, Principal user) {
        if (user != null) {
            User userDB = userService.findByUsername(user.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<ForumPost> posts = forumInterface.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(postName.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("match_posts", posts);

        return "sections/forum/matchForumPosts";
    }
}