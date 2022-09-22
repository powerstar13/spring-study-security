package com.sp.fc.web.config;

import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit implements InitializingBean {
    
    private final SpUserService userService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // 초기 데이터 준비
        if (!userService.findUser("user1").isPresent()) {
            
            SpUser user = userService.save(
                SpUser.builder()
                    .email("user1")
                    .password("1111")
                    .enabled(true)
                    .build()
            );
            
            userService.addAuthority(user.getUserId(), "ROLE_USER");
        }
        if (!userService.findUser("user2").isPresent()) {
            
            SpUser user = userService.save(
                SpUser.builder()
                    .email("user2")
                    .password("1111")
                    .enabled(true)
                    .build()
            );
            
            userService.addAuthority(user.getUserId(), "ROLE_USER");
        }
        if (!userService.findUser("admin").isPresent()) {
            
            SpUser user = userService.save(
                SpUser.builder()
                    .email("admin")
                    .password("1111")
                    .enabled(true)
                    .build()
            );
            
            userService.addAuthority(user.getUserId(), "ROLE_ADMIN");
        }
    }
}
