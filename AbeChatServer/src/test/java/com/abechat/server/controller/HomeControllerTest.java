package com.abechat.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    private final MockMvc mockMvc;
    private final HomeController homeController;

    @Autowired
    HomeControllerTest(MockMvc mockMvc, HomeController homeController) {
        this.mockMvc = mockMvc;
        this.homeController = homeController;
    }

    @Test
    void contextLoads() {
        assertNotNull(homeController);
    }

    @Test
    void indexReturnedWhenHomeViewRequested() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}