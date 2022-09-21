package com.sp.fc.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sp_user_authority")
@IdClass(SpAuthority.class) // 두 필드를 ID로 사용하기 위해 IdClass 애노테이션 사용
public class SpAuthority implements GrantedAuthority {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Id
    private String authority;
}
