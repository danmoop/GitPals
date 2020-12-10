package com.moople.gitpals.MainApplication.Model;

import lombok.Data;

@Data
public class Pair<T,V> {
    private T key;
    private V value;

    public Pair(T key, V value) {
        this.key = key;
        this.value = value;
    }
}