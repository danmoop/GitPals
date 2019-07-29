package com.moople.gitpals.MainApplication.Service;

import java.util.HashMap;
import java.util.Map;

public class Data
{
    // This data is imported in IndexController & ProjectController

    public static final String[] TECHS = { "Web design", "Mobile design", "Java", "C++",
            "Python", "Machine learning", "Deep learning", "Ionic",
            "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
            "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
            "C", "GLSL", "OpenGL", "HTML5", "C#", "Swift", "Big Data", "CSS",
            "Game modding", "Other"
    };

    public static Map<String, Boolean> technologiesMap = new HashMap<>();

    public static void initTechnologiesMap()
    {
        for (String tech : TECHS)
        {
            technologiesMap.put(tech, false);
        }
    }
}