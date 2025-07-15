package com.abechat.server.dao;

import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.IdUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserDao {
    private static final String GET_USER_BY_USERNAME = "select id, name, password from users where name = :username";
    private static final String SAVE_USER = "insert into users (id, name, password) values (:id, :name, :password)";
    private static final String USERNAME_EXISTS = "select 1 from users where name = :name";
    private final NamedParameterJdbcTemplate abeChatJdbcTemplate;

    @Autowired
    public UserDao(NamedParameterJdbcTemplate abeChatJdbcTemplate) {
        this.abeChatJdbcTemplate = abeChatJdbcTemplate;
    }

    public List<String> getAllUsernames() {
        return abeChatJdbcTemplate.query("select name from users", (rs, rowNum) -> rs.getString("name"));
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var params = new MapSqlParameterSource().addValue("username", username);
        try {
            return abeChatJdbcTemplate.queryForObject(GET_USER_BY_USERNAME, params, (rs, row) ->
                    new IdUser(UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getString("password")));
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    public boolean usernameExists(String username) {
        var params = new MapSqlParameterSource().addValue("name", username);

        try {
            Integer result = abeChatJdbcTemplate.queryForObject(USERNAME_EXISTS, params, Integer.class);
            return result != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public void insertNewUser(IdUser user) throws UsernameAlreadyExists {
        var params = new MapSqlParameterSource()
                .addValue("id", user.id().toString())
                .addValue("name", user.getUsername())
                .addValue("password", user.getPassword());

        try {
            abeChatJdbcTemplate.update(SAVE_USER, params);
        } catch (DuplicateKeyException e) {
            throw new UsernameAlreadyExists(user.getUsername());
        }
    }
}
