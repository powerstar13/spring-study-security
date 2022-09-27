package com.sp.fc.user.service;

import com.sp.fc.user.domain.SpAuthority;
import com.sp.fc.user.domain.SpOAuth2User;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.repository.SpOAuth2UserRepository;
import com.sp.fc.user.repository.SpUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SpUserService implements UserDetailsService {
    
    private final SpUserRepository userRepository;
    
    private final SpOAuth2UserRepository oAuth2UserRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }
    
    public Optional<SpUser> findUser(String email) {
        return userRepository.findUserByEmail(email);
    }
    
    public SpUser save(SpUser user) {
        return userRepository.save(user);
    }
    
    public void addAuthority(Long userId, String authority) {
        
        userRepository.findById(userId)
            .ifPresent(user -> {
                
                SpAuthority newRole = new SpAuthority(user.getUserId(), authority);
                
                HashSet<SpAuthority> authorities = new HashSet<>();
                
                if (user.getAuthorities() != null && !user.getAuthorities().contains(newRole)) {
                    authorities.addAll(user.getAuthorities());
                }
                authorities.add(newRole);
                
                user.setAuthorities(authorities);
                this.save(user);
            });
    }
    
    public void removeAuthority(Long userId, String authority) {
        
        userRepository.findById(userId)
            .ifPresent(user -> {
            
                if (user.getAuthorities() == null) return;
    
                SpAuthority targetRole = new SpAuthority(user.getUserId(), authority);
                
                if (user.getAuthorities().contains(targetRole)) {
                    
                    user.setAuthorities(
                        user.getAuthorities().stream()
                            .filter(auth -> !auth.equals(targetRole))
                            .collect(Collectors.toSet())
                    );
                    this.save(user);
                }
            });
    }
    
    public SpUser loadUser(SpOAuth2User oAuth2User) {
    
        SpOAuth2User dbUser = oAuth2UserRepository.findById(oAuth2User.getOauth2UserId())
            .orElseGet(() -> {
    
                SpUser user = SpUser.builder()
                    .email(oAuth2User.getEmail())
                    .enabled(true)
                    .password("")
                    .build();
                
                userRepository.save(user);
                
                this.addAuthority(user.getUserId(), "ROLE_USER");
                
                oAuth2User.setUserId(user.getUserId());
                oAuth2User.setCreated(LocalDateTime.now());
    
                return oAuth2UserRepository.save(oAuth2User);
            });
    
        return userRepository.findById(dbUser.getUserId()).get();
    }
}
