package com.sp.fc.web.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    
    private SpUserService userService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    public JWTLoginFilter(AuthenticationManager authenticationManager, SpUserService userService) {
        super(authenticationManager);
        this.userService = userService;
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
        
        if (userLogin.getRefreshToken() == null) { // 리프레시 토큰이 없다면
            
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userLogin.getUsername(),
                userLogin.getPassword(),
                null
            );
    
            // user details... 추가적으로 처리할 것이 있다면 여기서 처리하면 된다.
    
            return getAuthenticationManager().authenticate(token);
        } else { // 리프레시 토큰이 왔다면
            VerifyResult verify = JWTUtil.verify(userLogin.getRefreshToken());
            
            if (verify.isSuccess()) { // 유효한 토큰이라면
    
                SpUser user = (SpUser) userService.loadUserByUsername(verify.getUsername());
                return new UsernamePasswordAuthenticationToken(
                    user,
                    user.getAuthorities()
                );
            } else {
                throw new TokenExpiredException("refresh token expired");
            }
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException
    {
        SpUser user = (SpUser) authResult.getPrincipal();

        response.setHeader("auth_token", JWTUtil.makeAuthToken(user));
        response.setHeader("refresh_token", JWTUtil.makeRefreshToken(user));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        response.getOutputStream().write(objectMapper.writeValueAsBytes(user));
    }
}
