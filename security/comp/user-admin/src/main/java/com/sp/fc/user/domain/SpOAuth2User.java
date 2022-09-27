package com.sp.fc.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sp_oauth2_user")
public class SpOAuth2User {
    
    @Id
    private String oauth2UserId; // google-{id}, naver-{id}
    
    private Long userId; // SpUser
    
    private OAuth2Provider provider;
    
    private String name;
    private String email;
    
    private LocalDateTime created;
    
    public static enum OAuth2Provider {
        
        google {
            @Override
            public SpOAuth2User convert(OAuth2User user) {
                
                return SpOAuth2User.builder()
                    .oauth2UserId(String.format("%s_%s", name(), user.getAttribute("sub")))
                    .provider(google)
                    .email(user.getAttribute("email"))
                    .name(user.getAttribute("name"))
                    .created(LocalDateTime.now())
                    .build();
            }
        },
        naver {
            @Override
            public SpOAuth2User convert(OAuth2User user) {
    
                Map<String, Object> resp = user.getAttribute("response");
    
                return SpOAuth2User.builder()
                    .oauth2UserId(String.format("%s_%s", name(), resp.get("id")))
                    .provider(naver)
                    .email(String.valueOf(resp.get("email")))
                    .name(String.valueOf(resp.get("name")))
                    .created(LocalDateTime.now())
                    .build();
            }
        };
        
        public abstract SpOAuth2User convert(OAuth2User user);
    }
}
