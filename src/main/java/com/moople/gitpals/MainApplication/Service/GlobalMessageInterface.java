package com.moople.gitpals.MainApplication.Service;

import com.moople.gitpals.MainApplication.Model.GlobalMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GlobalMessageInterface extends MongoRepository<GlobalMessage, String> {
    List<GlobalMessage> findAll();
}