package com.sp.fc.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fc.user.domain.SpUser;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/login"); // POST 로그인을 처리해준다.
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException
    {
        UserLoginForm userLogin = objectMapper.readValue(request.getInputStream(), UserLoginForm.class);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            userLogin.getUsername(),
            userLogin.getPassword(),
            null
        );

        // user details... 추가적으로 처리할 것이 있다면 여기서 처리하면 된다.

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException
    {
        SpUser user = (SpUser) authResult.getPrincipal();

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + JWTUtil.makeAuthToken(user)); // JWT의 경우 Bearer를 붙이는 것이 규약이다.
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        response.getOutputStream().write(objectMapper.writeValueAsBytes(user));
    }
}
