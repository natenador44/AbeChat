package com.abechat.server.controller;

import com.abechat.server.TestUserSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    private final MockMvc mockMvc;
    private final LoginController loginController;
    private final TestUserSetup testUserSetup;

    @Autowired
    LoginControllerTest(MockMvc mockMvc, LoginController loginController, TestUserSetup testUserSetup) {
        this.mockMvc = mockMvc;
        this.loginController = loginController;
        this.testUserSetup = testUserSetup;
    }

    @Test
    void contextLoads() {
        assertNotNull(loginController);
    }

    @Test
    void loginViewRequestReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // TODO the redirect URL will probably change in the near future, but I wanted the test here anyway.
    // If you change the redirect url for this login endpoint in the future, know it is ok to change this test (and its name).
    @Test
    @Sql(scripts = {"classpath:clear_tables.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successfulLoginRedirectsToHomePage() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                post("/login/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .formField("username", TestUserSetup.GARY_USERNAME)
                        .formField("password", TestUserSetup.GARY_PASSWORD)
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginRedirectsToLoginViewWithErrorIfUserDoesNotExist() throws Exception {
        mockMvc.perform(
                        post("/login/")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .formField("username", TestUserSetup.GARY_USERNAME)
                                .formField("password", TestUserSetup.GARY_PASSWORD)
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @Sql(scripts = {"classpath:clear_tables.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void loginRedirectsToLoginViewWithErrorIfWrongPasswordIsGiven() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                        post("/login/")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .formField("username", TestUserSetup.GARY_USERNAME)
                                .formField("password", "wrong")
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

}