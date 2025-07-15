package com.abechat.server.api;

import com.abechat.server.TestUserSetup;
import com.abechat.server.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiControllerTest {
    private static final String TEST_API_KEY = "test-api-key";
    private final UserController userController;
    private final TestUserSetup testUserSetup;
    private final MockMvc mockMvc;

    @Autowired
    public UserApiControllerTest(UserController userController, TestUserSetup testUserSetup, MockMvc mockMvc) {
        this.userController = userController;
        this.testUserSetup = testUserSetup;
        this.mockMvc = mockMvc;
    }

    @Test
    void contextLoads() {
        assertNotNull(userController);
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void loginRequiresAPIKey() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUserSetup.loginRequestAsJson(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD))
        ).andExpect(status().is(401))
                .andExpect(content().string("Missing API Key"));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successfulLoginReturns200() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                        post("/api/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUserSetup.loginRequestAsJson(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD))
                                .header(ApiKeyAuthFilter.AUTH_TOKEN_HEADER_NAME, TEST_API_KEY)
                ).andExpect(status().is(200));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void incorrectPasswordReturns403() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUserSetup.loginRequestAsJson(TestUserSetup.GARY_USERNAME, "wrong password"))
                        .header(ApiKeyAuthFilter.AUTH_TOKEN_HEADER_NAME, TEST_API_KEY)
        ).andExpect(status().is(403));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void unknownUsernameReturns403() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUserSetup.loginRequestAsJson("not in db", "wrong password"))
                        .header(ApiKeyAuthFilter.AUTH_TOKEN_HEADER_NAME, TEST_API_KEY)
        ).andExpect(status().is(403));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createUserRequiresAPIKey() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);

        mockMvc.perform(
                        post("/api/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUserSetup.loginRequestAsJson(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD))
                ).andExpect(status().is(401))
                .andExpect(content().string("Missing API Key"));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createUserSuccessReturns201() throws Exception {
        mockMvc.perform(
                post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUserSetup.createUserRequestAsJson(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD))
                        .header(ApiKeyAuthFilter.AUTH_TOKEN_HEADER_NAME, TEST_API_KEY)
        ).andExpect(status().is(201))
                .andExpect(redirectedUrl("/abechat/login/"));
    }

    @Test
    @Sql(scripts = { "classpath:clear_tables.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createUserExistingUsernameReturns400() throws Exception {
        testUserSetup.insertNewUser(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);
        mockMvc.perform(
                        post("/api/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUserSetup.createUserRequestAsJson(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD))
                                .header(ApiKeyAuthFilter.AUTH_TOKEN_HEADER_NAME, TEST_API_KEY)
                ).andExpect(status().is(400));
    }
}