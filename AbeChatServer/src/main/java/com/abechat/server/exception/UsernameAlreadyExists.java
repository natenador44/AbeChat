package com.abechat.server.exception;

public class UsernameAlreadyExists extends Exception {
    public UsernameAlreadyExists(String username) {
        super(username + " already exists");
    }
}
