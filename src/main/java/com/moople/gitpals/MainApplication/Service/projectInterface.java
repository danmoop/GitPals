package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface projectInterface extends MongoRepository<Project, String>
{
    public Project findByTitle(String title);
}