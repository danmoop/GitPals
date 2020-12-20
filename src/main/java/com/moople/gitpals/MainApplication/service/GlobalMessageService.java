package com.moople.gitpals.MainApplication.service;

import com.moople.gitpals.MainApplication.model.GlobalMessage;
import com.moople.gitpals.MainApplication.repository.GlobalMessageRepository;
import com.moople.gitpals.MainApplication.service.interfaces.GlobalMessageInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalMessageService implements GlobalMessageInterface {

    @Autowired
    private GlobalMessageRepository globalMessageRepository;


    @Override
    public List<GlobalMessage> findAll() {
        return globalMessageRepository.findAll();
    }

    @Override
    public void save(GlobalMessage globalMessage) {
        globalMessageRepository.save(globalMessage);
    }

    @Override
    public void delete(GlobalMessage globalMessage) {
        globalMessageRepository.delete(globalMessage);
    }
}

