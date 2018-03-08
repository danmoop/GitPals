package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface userInterface extends MongoRepository<User, String>
{
    public User findByUsername(String username);
}
