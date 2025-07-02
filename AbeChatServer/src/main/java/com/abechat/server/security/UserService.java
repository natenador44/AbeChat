package com.abechat.server.security;

import com.abechat.server.dao.UserDao;
import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService, UserPersistenceService {
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserDao userDao) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.loadUserByUsername(username);
    }

    @Override
    public void createNew(Request.NewUser newUser) throws UsernameAlreadyExists {
        var hashedUser = newUser.intoHashedUser(passwordEncoder);
        // newUser.password is no longer accessible here

        if (userDao.usernameExists(hashedUser.username())) {
            throw new UsernameAlreadyExists(hashedUser.username());
        }

        userDao.insertNewUser(hashedUser.intoIdUser());
    }

    public List<String> getAllUsernames() {
        return userDao.getAllUsernames();
    }
}
