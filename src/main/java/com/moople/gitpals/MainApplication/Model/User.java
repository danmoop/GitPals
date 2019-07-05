package com.moople.gitpals.MainApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User
{
    /**
     * @param id is set as 'null' in the constructor, but initialized then to String by @Id annotation
     */
    @Id
    private String id;

    private String username;
    private String GithubAccountLink;
    private String country;
    private String info;
    private Map<String, Boolean> languagesKnows;
    private List<String> projects;
    private List<String> projectsAppliedTo;
    private List<Message> messages;
}