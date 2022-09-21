package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpUserService spUserService;
    
    public SecurityConfig(SpUserService spUserService) {
        this.spUserService = spUserService;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(spUserService);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Deprecated: Test 시에는 사용해도 좋으나, 운영 상에서는 사용 금지해야 함
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
                    .loginProcessingUrl("/loginprocess")
                    .permitAll() // 사용자 지정 로그인 페이지의 경우 permitAll() 처리를 안할 경우 무한 루프에 빠질 위험이 있으므로 반드시 추가해야 함
                    .defaultSuccessUrl("/", false) // 로그인 성공 시 root 페이지로 보내고, alwaysUse는 false 처리하는 것이 좋다.
                    .failureUrl("/login-error") // 로그인 실패 시 Redirect 시킬 페이지 경로
            ) // 기본적으로 formLogin()을 사용하고 경로를 지정 안할 경우, User 로그인 페이지가 DefaultLoginPageGeneratingFilter, DefaultLogoutnPageGeneratingFilter에 의해 노출된다.
            .logout(logout -> logout.logoutSuccessUrl("/")) // 로그아웃 시 Redirect할 경로
            .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .requestMatchers(
                PathRequest.toStaticResources().atCommonLocations(), // Web Resources는 모두 시큐리티에서 제외하도록 처리
                PathRequest.toH2Console() // YAML에 기입된 콘솔에 접속할 수 있는 h2-console 경로를 열어준다.
            );
    }
}
