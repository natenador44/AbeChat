package com.abechat.server.config;

import com.abechat.server.exception.EnvironmentInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile("release")
@Configuration
public class ReleaseDataSourceConfig {
    public static final String DB_URL_ENV = "DB_URL";
    public static final String DB_USER_ENV = "DB_USER";
    public static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    @Autowired
    public ReleaseDataSourceConfig(
            @Value("${" + DB_URL_ENV + "}") String dbUrl,
            @Value("${" + DB_USER_ENV + "}") String dbUser,
            @Value("${" + DB_PASSWORD_ENV + "}") String dbPassword
    ) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");

        if (dbUrl == null || dbUrl.isEmpty()) {
            throw EnvironmentInitException.noDbUrlSpecified();
        }
        dataSource.setUrl(dbUrl);

        if (dbUser == null || dbUser.isEmpty()) {
            throw EnvironmentInitException.noDbUserSpecified();
        }
        dataSource.setUsername(dbUser);

        if (dbPassword == null || dbPassword.isEmpty()) {
            throw EnvironmentInitException.noDbPasswordSpecified();
        }
        dataSource.setPassword(dbPassword);

        return dataSource;
    }
}
