package com.moople.gitpals.MainApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "projects")
public class Project
{
    /**
     * @param id is set as 'null' in the constructor, but initialized then to String by @Id annotation
     */
    @Id
    private String id;

    private String title;
    private String description;
    private String githubProjectLink;
    private String authorName;
    private List<String> requirements;
    private List<String> usersSubmitted;
}