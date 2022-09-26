package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class AdvancedSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpUserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JWTLoginFilter loginFilter = new JWTLoginFilter(authenticationManager()); // 로그인을 처리해주는 필터
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(), userService); // 로그인된 토큰을 매번 검증해줄 필터

        http
            .csrf().disable() // CSRF 토큰을 구현하는 것은 매우 번거롭고 어려운 작업이므로 사용하지 않는다.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT를 사용할 것이기 때문에 세션은 사용하지 않는다.
            )
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(checkFilter, BasicAuthenticationFilter.class);
    }
}
