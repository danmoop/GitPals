package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.Message;
import com.moople.gitpals.MainApplication.Model.Project;
import com.moople.gitpals.MainApplication.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public static final List<Project> EMPTY_PROJECT_LIST = new ArrayList<>();
    public static final List<User> EMPTY_USER_LIST = new ArrayList<>();
    public static final List<Message> EMPTY_MESSAGES_LIST = new ArrayList<>();
    public static final User EMPTY_USER = new User();
    public static final Project EMPTY_PROJECT = new Project();

    // This key is different from a real one used on gitpals.com
    public static final String ENCRYPTION_KEY = "5DA99C95FA5C10A2CDA3E4FEF53A85CC2DC833A1";
}