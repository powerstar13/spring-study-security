package com.sp.fc.web.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthDetails customAuthDetails;

    public SecurityConfig(CustomAuthDetails customAuthDetails) {
        this.customAuthDetails = customAuthDetails;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser(
                User.withDefaultPasswordEncoder() // 해당 비밀번호 인코딩은 안전하지 않으므로 Deprecated 되었지만, test에 한정해서 사용
                    .username("user1")
                    .password("1111")
                    .roles("USER")
            ).withUser(
                User.withDefaultPasswordEncoder()
                    .username("admin")
                    .password("2222")
                    .roles("ADMIN")
            );
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        // 관리자는 일반 유저의 페이지에도 접근할 수 있도록 조정
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(request -> {
                request
                    .antMatchers("/").permitAll() // root 페이지를 제외하고는 모두 막아두도록 처리
                    .anyRequest().authenticated();
            })
            .formLogin(login ->
                login.loginPage("/login")
                    .permitAll() // 사용자 지정 로그인 페이지의 경우 permitAll() 처리를 안할 경우 무한 루프에 빠질 위험이 있으므로 반드시 추가해야 함
                    .defaultSuccessUrl("/", false) // 로그인 성공 시 root 페이지로 보내고, alwaysUse는 false 처리하는 것이 좋다.
                    .failureUrl("/login-error") // 로그인 실패 시 Redirect 시킬 페이지 경로
                    .authenticationDetailsSource(customAuthDetails)
            ) // 기본적으로 formLogin()을 사용하고 경로를 지정 안할 경우, User 로그인 페이지가 DefaultLoginPageGeneratingFilter, DefaultLogoutnPageGeneratingFilter에 의해 노출된다.
            .logout(logout -> logout.logoutSuccessUrl("/")) // 로그아웃 시 Redirect할 경로
            .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .requestMatchers(
                PathRequest.toStaticResources().atCommonLocations() // Web Resources는 모두 시큐리티에서 제외하도록 처리
            );
    }
}
