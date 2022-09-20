package com.sp.fc.web.student;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class StudentManager implements AuthenticationProvider, InitializingBean {
    
    private HashMap<String, Student> studentDB = new HashMap<>(); // 실제 DB가 있다면 DB에 갔다와야겠지만, 지금은 test이므로 HashMap으로 DB를 대체함
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        
        if (studentDB.containsKey(token.getName())) {
            
            Student student = studentDB.get(token.getName());
            
            return StudentAuthenticationToken.builder()
                .principal(student)
                .details(student.getUsername())
                .authenticated(true)
                .build();
        }
        
        return null;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    
        Set.of(
            new Student("hong", "홍길동", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"))),
            new Student("kang", "강아지", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"))),
            new Student("rang", "호랑이", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
        ).forEach(student ->
            studentDB.put(student.getId(), student)
        );
    }
}
