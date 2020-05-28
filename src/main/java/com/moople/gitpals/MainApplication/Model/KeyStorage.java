package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data
@Document(collection = "keyStorage")
public class KeyStorage {

    @Id
    private String id;

    private String username;
    private String key;

    public KeyStorage(String username) {
        this.username = username;
        this.key = generateKey();
    }

    public static String generateKey() {
        return Encrypt.MD5(new Date().getTime() + UUID.randomUUID().toString() + Math.random());
    }
}