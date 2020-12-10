package com.moople.gitpals.MainApplication.Model;

import com.moople.gitpals.MainApplication.Service.Encrypt;
import lombok.Data;

import java.util.Date;

@Data
public class Notification {
    private String text;
    private String key;

    public Notification(String text) {
        this.text = text;
        this.key = Encrypt.MD5(Math.random() + new Date().getTime() + text);
    }
}