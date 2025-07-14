package com.abechat.server.dao;

import com.abechat.server.TestUserSetup;
import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.IdUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDaoTest {

    private final UserDao userDao;
    private final TestUserSetup testUserSetup;

    @Autowired
    public UserDaoTest(UserDao userDao, TestUserSetup testUserSetup) {
        this.userDao = userDao;
        this.testUserSetup = testUserSetup;
    }

    @Test
    void contextLoads() {
        assertNotNull(userDao);
    }

    @Test
    void loadUserByUsernameThrowsUsernameNotFoundExceptionIfUsernameDoesNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> userDao.loadUserByUsername("not in db"));
    }

    @Test
    @Sql(scripts = "classpath:clear_tables.sql", executionPhase =  Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void insertNewUserThrowsUsernameAlreadyExistsExceptionIfUsernameAlreadyExists() {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        assertThrows(UsernameAlreadyExists.class, () -> userDao.insertNewUser(new IdUser(UUID.randomUUID(), TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD)));
    }

    @Test
    @Sql(scripts = "classpath:clear_tables.sql", executionPhase =  Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void usernameExists() {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        assertTrue(userDao.usernameExists(TestUserSetup.GARY_USERNAME));
        assertFalse(userDao.usernameExists("not in db"));
    }
}