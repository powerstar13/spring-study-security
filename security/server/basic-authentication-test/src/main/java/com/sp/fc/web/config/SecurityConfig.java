package com.sp.fc.web.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    BasicAuthenticationFilter filter;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser(
                User.withDefaultPasswordEncoder()
                    .username("user1")
                    .password("1111")
                    .roles("USER")
                    .build()
            );
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // CSRF를 disable 처리해야 POST 방식을 사용할 수 있다.
            .authorizeRequests().anyRequest().authenticated() // 모든 요청에 대해 인증을 진행한다.
            .and()
            .httpBasic();
    }
    
}
