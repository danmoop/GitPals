package com.moople.gitpals.MainApplication.Controller;

import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class SortController
{
    static List<Project> filterProjects(List<Project> projects, Predicate<Project> p)
    {
        List<Project> result = new ArrayList<>();

        for (Project project: projects)
        {
            if(p.test(project))
                result.add(project);
        }

        return result;
    }

    static List<String> filterUsers(List<User> users, Predicate<User> p)
    {
        List<String> result = new ArrayList<>();

        for (User user: users)
        {
            if(p.test(user))
                result.add(user.getUsername());
        }

        return result;
    }
}