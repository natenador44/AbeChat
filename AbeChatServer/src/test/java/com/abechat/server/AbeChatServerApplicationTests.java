package com.abechat.server;

import com.abechat.server.controller.LoginController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AbeChatServerApplicationTests {
    private final LoginController loginController;

    @Autowired
    public AbeChatServerApplicationTests(LoginController loginController) {
        this.loginController = loginController;
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(loginController);
    }

}
