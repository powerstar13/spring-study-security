package com.sp.fc.web.config;

import com.sp.fc.user.domain.SpOAuth2User;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SpOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    
    private final SpUserService userService;
    
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException
    {
        Object principal = authentication.getPrincipal();
    
        // WARN: OAuth2USer 객체가 상위이고 OidcUser 객체가 하위이기 때문에 OidcUser를 먼저 확인해야 한다.
        if (principal instanceof OAuth2User) {
            if (principal instanceof OidcUser) {
                // google
                SpOAuth2User oauth = SpOAuth2User.OAuth2Provider.google.convert((OAuth2User) principal);
                SpUser user = userService.loadUser(oauth);
        
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                    )
                );
            } else {
                // naver, kakao, facebook
                SpOAuth2User oauth = SpOAuth2User.OAuth2Provider.naver.convert((OAuth2User) principal);
                SpUser user = userService.loadUser(oauth);
        
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                    )
                );
            }
    
            System.out.println(principal);
    
            // Success Url 지정
            request.getRequestDispatcher("/").forward(request, response);
        }
    }
}
