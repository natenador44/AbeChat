package com.abechat.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class TestDataConfig {
    @Bean
    public DataSource parkourDataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setUsername("sa");
        dataSource.setSuppressClose(true);

        return dataSource;
    }
}
