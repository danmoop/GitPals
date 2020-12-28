package com.moople.gitpals.MainApplication.controller;

import com.moople.gitpals.MainApplication.model.ForumPost;
import com.moople.gitpals.MainApplication.model.Project;
import com.moople.gitpals.MainApplication.model.User;
import com.moople.gitpals.MainApplication.service.ForumService;
import com.moople.gitpals.MainApplication.service.ProjectService;
import com.moople.gitpals.MainApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ForumService forumService;

    /**
     * This request is handled when user wants to see a search page to find a project or a user
     *
     * @return html page where users can find a project or a user by name
     */
    @GetMapping("/search")
    public String searchPage(Model model, Principal auth) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

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
    public String foundUsers(@RequestParam String username, Model model, Principal auth) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<String> matchUsers = userService.matchUsersByUsername(username)
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

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
    public String foundProjects(@RequestParam String projectName, Model model, Principal auth) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<String> matchProjects = projectService.matchProjectsByProjectTitle(projectName)
                .stream()
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
    public String usersBySkills(@RequestParam(name = "skill", required = false) List<String> skills, Model model, Principal auth, RedirectAttributes redirectAttributes) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (skills == null || skills.size() == 0) {
            redirectAttributes.addFlashAttribute("msg", "You should choose some options!");
            return "redirect:/search";
        }

        skills = skills.stream()
                .filter(s -> !s.trim().equals(""))
                .collect(Collectors.toList());

        Set<String> users = userService.findBySkillList(skills);

        model.addAttribute("match_users", users);

        return "sections/users/matchUsers";
    }

    /**
     * This request is handled when user wants to sort projects by technologies
     * They will be sorted and displayed
     *
     * @param techs              is a list of technologies checkboxes user select manually
     * @param model              will contains results of the search
     * @param auth               is user's authentication object
     * @param redirectAttributes is responsible for redirecting user back if no technologies are given
     * @return a list of projects according to user's preference
     **/
    @PostMapping("/matchProjectsByTechnologies")
    public String sortProjectsByTechnologies(
            @RequestParam(name = "tech", required = false) List<String> techs,
            Model model,
            Principal auth,
            RedirectAttributes redirectAttributes
    ) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (techs == null || techs.size() == 0) {
            redirectAttributes.addFlashAttribute("msg", "You should choose some options!");
            return "redirect:/search";
        }

        techs = techs.stream()
                .filter(s -> !s.trim().equals(""))
                .collect(Collectors.toList());

        Set<String> matchProjects = projectService.matchProjectsByTechnologies(techs)
                .stream()
                .map(Project::getTitle).collect(Collectors.toSet());

        model.addAttribute("match_projects", matchProjects);

        return "sections/projects/matchProjects";
    }

    /**
     * This function returns a list of projects with roles specified by a user
     *
     * @param roles              is a list of roles specified by a user
     * @param model              will contains results of the search
     * @param auth               is user's authentication object
     * @param redirectAttributes is responsible for redirecting user back if no technologies are given
     * @return a page, which contains results of the search
     */
    @PostMapping("/matchProjectsByRoles")
    public String matchProjectsByRoles(
            @RequestParam(name = "role", required = false) List<String> roles,
            Model model,
            Principal auth,
            RedirectAttributes redirectAttributes
    ) {
        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        if (roles == null || roles.size() == 0) {
            redirectAttributes.addFlashAttribute("msg", "You should choose some options!");
            return "redirect:/search";
        }

        roles = roles.stream()
                .filter(s -> !s.trim().equals(""))
                .collect(Collectors.toList());

        Set<String> matchProjects = projectService.matchProjectsByRoles(roles)
                .stream()
                .map(Project::getTitle).collect(Collectors.toSet());

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
    public String findForumPosts(@RequestParam String postName, Model model, Principal auth) {

        if (auth != null) {
            User userDB = userService.findByUsername(auth.getName());

            if (userDB.isBanned()) {
                return "sections/users/banned";
            }
        }

        List<ForumPost> posts = forumService.matchForumPostsByTitle(postName);

        model.addAttribute("match_posts", posts);

        return "sections/forum/matchForumPosts";
    }
}
