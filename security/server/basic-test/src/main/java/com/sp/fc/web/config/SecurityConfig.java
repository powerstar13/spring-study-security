package com.sp.fc.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // prePost로 권한 체크를 하도록 명시
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 사용자 추가하기
        auth.inMemoryAuthentication()
            .withUser(
                User.builder()
                    .username("user2")
                    .password(passwordEncoder().encode("2222"))
                    .roles("USER")
                    .build()
            )
            .withUser(User.builder()
                .username("admin")
                .password(passwordEncoder().encode("3333"))
                .roles("ADMIN")
                .build()
            );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 패스워드 인코딩
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests((requests) ->
            requests
                .antMatchers("/").permitAll() // index인 홈페이지에 대해서는 사용자 권한 체크 없이 누구나 접근할 수 있도록 처리
                .anyRequest().authenticated()
        );
        http.formLogin();
        http.httpBasic();
    }
}
