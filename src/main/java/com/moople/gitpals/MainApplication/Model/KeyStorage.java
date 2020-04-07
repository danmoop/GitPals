package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
public class KeyStorage {

    @Id
    private String id;

    private String username;
    private String key;

    public KeyStorage(String username) {
        this.username = username;
        this.key = Encrypt.MD5(username + new Date().getTime() + Math.random());
    }

    @Override
    public String toString() {
        return "KeyStorage{" +
                "username='" + username + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}