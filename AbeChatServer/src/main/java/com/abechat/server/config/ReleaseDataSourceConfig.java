package com.abechat.server.config;

import com.abechat.server.exception.EnvironmentInitException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile("release")
@Configuration
public class ReleaseDataSourceConfig {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");

        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw EnvironmentInitException.noDbUrlSpecified();
        }

        dataSource.setUrl(dbUrl);

        return dataSource;
    }
}
