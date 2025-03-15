package dev.hyein.reactivesample.ex3_webflux;

import lombok.Getter;

@Getter
public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }
}
