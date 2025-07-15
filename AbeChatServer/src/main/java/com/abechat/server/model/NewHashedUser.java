package com.abechat.server.model;

import java.util.UUID;

/**
 * A new password that no longer contains the plain text password given in a {@link Request.NewUser} request
 * and is ready to go through the registration process
 * @param username
 * @param hashedPassword
 */
public record NewHashedUser(String username, String hashedPassword) {
    public IdUser intoIdUser() {
        return new IdUser(UUID.randomUUID(), username, hashedPassword);
    }
}
