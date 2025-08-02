package com.abechat.server.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * This security chain filter is run first (@Order(1)). This matches on all endpoints starting with
     * '/api' and runs them through a {@link ApiKeyAuthFilter}, which requires an Api key to be present
     * in the header of the request. Currently, this just checks for the existence of one. Later, actual API keys
     * will need to be sent and verified in order to gain access
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ApiKeyAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Profile("release")
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityRelease(HttpSecurity http) throws Exception {
        return enableCommonWebSecurity(http)
                .build();
    }

    @Profile("dev")
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityDev(HttpSecurity http) throws Exception {
        enableCommonWebSecurity(http)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    /**
     * Configures all the public web pages, and throws the rest behind spring security's session management (i.e. requiring
     * the user to have logged in first)
     */
    private static HttpSecurity enableCommonWebSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/", "/login", "/createUser", "/error").permitAll()
                .anyRequest().authenticated())
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );
        return http;
    }

    /**
     * Creates our own custom authentication manager so we can be picky about how our passwords are hashed
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}