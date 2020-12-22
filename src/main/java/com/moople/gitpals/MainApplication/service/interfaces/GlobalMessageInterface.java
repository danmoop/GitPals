package com.moople.gitpals.MainApplication.service.interfaces;

import com.moople.gitpals.MainApplication.model.GlobalMessage;

import java.util.List;

public interface GlobalMessageInterface {
    List<GlobalMessage> findAll();

    void save(GlobalMessage globalMessage);

    void delete(GlobalMessage globalMessage);

    void deleteAll();
}
