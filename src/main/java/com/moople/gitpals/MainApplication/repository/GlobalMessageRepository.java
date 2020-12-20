package com.moople.gitpals.MainApplication.repository;

import com.moople.gitpals.MainApplication.model.GlobalMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GlobalMessageRepository extends MongoRepository<GlobalMessage, String> {
    List<GlobalMessage> findAll();
}