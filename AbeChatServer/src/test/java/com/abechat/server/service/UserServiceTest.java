package com.abechat.server.service;

import com.abechat.server.TestUserSetup;
import com.abechat.server.dao.UserDao;
import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.IdUser;
import com.abechat.server.model.Request;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void loadUserByUsernameThrowsExceptionWhenUsernameNotFound() {
        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(TestUserSetup.GARY_PASSWORD)).thenReturn("secret");

        var userDao = mock(UserDao.class);
        when(userDao.loadUserByUsername(TestUserSetup.GARY_USERNAME)).thenThrow(UsernameNotFoundException.class);

        var userService = new UserService(passwordEncoder, userDao);

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(TestUserSetup.GARY_USERNAME));
    }

    @Test
    void loadUserByUsernameReturnsUserWithoutModificationIfFound() {
        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(TestUserSetup.GARY_PASSWORD)).thenReturn("secret");

        var userDao = mock(UserDao.class);
        var expectedUser = new IdUser(UUID.randomUUID(), TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);
        when(userDao.loadUserByUsername(TestUserSetup.GARY_USERNAME)).thenReturn(expectedUser);

        var userService = new UserService(passwordEncoder, userDao);

        var actualUser = userService.loadUserByUsername(TestUserSetup.GARY_USERNAME);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void createUserThrowsExceptionWhenUsernameAlreadyExists() {
        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(TestUserSetup.GARY_PASSWORD)).thenReturn("secret");

        var userDao = mock(UserDao.class);
        when(userDao.usernameExists(TestUserSetup.GARY_USERNAME)).thenReturn(true);

        var userService = new UserService(passwordEncoder, userDao);

        assertThrows(UsernameAlreadyExists.class, () ->
                userService.createNew(new Request.NewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD)));
    }

    @Test
    void createUserInsertsNewUserIfThatUsernameDoesNotExist() throws UsernameAlreadyExists {
        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(TestUserSetup.GARY_PASSWORD)).thenReturn("secret");

        var userDao = mock(UserDao.class);
        when(userDao.usernameExists(TestUserSetup.GARY_USERNAME)).thenReturn(false);

        var userService = new UserService(passwordEncoder, userDao);

        userService.createNew(new Request.NewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD));

        verify(userDao, times(1)).insertNewUser(any(IdUser.class));
    }
}