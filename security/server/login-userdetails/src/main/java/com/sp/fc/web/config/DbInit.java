package com.sp.fc.web.config;

import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit implements InitializingBean {
    
    private final SpUserService userService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        if (!userService.findUser("user@test.com").isPresent()) {
            
            SpUser user = userService.save(
                SpUser.builder()
                    .email("user@test.com")
                    .password("1111")
                    .enabled(true)
                    .build()
            );
            
            userService.addAuthority(user.getUserId(), "ROLE_USER");
        }
    }
}
