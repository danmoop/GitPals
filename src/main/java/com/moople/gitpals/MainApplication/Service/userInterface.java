package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface userInterface extends MongoRepository<User, String>
{
    User findByUsername(String username);

    List<User> findAll();
}
