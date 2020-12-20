package com.moople.gitpals.MainApplication.repository;

import com.moople.gitpals.MainApplication.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByTitle(String title);

    List<Project> findAll();
}