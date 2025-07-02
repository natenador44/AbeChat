package com.abechat.server.security;

import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.Request;

public interface UserPersistenceService {
    void createNew(Request.NewUser existingUser) throws UsernameAlreadyExists;
}
