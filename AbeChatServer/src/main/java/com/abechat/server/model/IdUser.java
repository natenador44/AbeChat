package com.abechat.server.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A User that has been assigned an ID.
 * This could mean the user came from a database, or is being created for the first time and is about to be saved to the database
 * @param id
 * @param username
 * @param passwordHash
 */
public record IdUser(UUID id, String username, String passwordHash) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
