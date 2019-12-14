package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface ProjectInterface extends MongoRepository<Project, String> {
    Project findByTitle(String title);
}