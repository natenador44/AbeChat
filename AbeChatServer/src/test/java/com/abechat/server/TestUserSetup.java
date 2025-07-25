package com.abechat.server;

import com.abechat.server.model.IdUser;
import com.abechat.server.model.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestUserSetup {
    public static final String GARY_USERNAME = "gary";
    public static final String GARY_PASSWORD = "secret";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final NamedParameterJdbcTemplate abechatJdbcTemplate;
    private final PasswordEncoder passwordEncoder;


    public static String loginRequestAsJson(String username, String password) {
        try {
            return MAPPER.writeValueAsString(new Request.Login(username, password));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createUserRequestAsJson(String username, String password) {
        try {
            return MAPPER.writeValueAsString(new Request.NewUser(username, password));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public TestUserSetup(NamedParameterJdbcTemplate abechatJdbcTemplate, PasswordEncoder passwordEncoder) {
        this.abechatJdbcTemplate = abechatJdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void insertNewUser(String username, String password) {
        var id = UUID.randomUUID().toString();
        var passwordHash = passwordEncoder.encode(password);

        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("username", username)
                .addValue("passwordHash", passwordHash);

        abechatJdbcTemplate.update("insert into users (id, name, password) values (:id, :username, :passwordHash)", params);
    }
}
