package com.sp.fc.web.teacher;

import com.sp.fc.web.student.Student;
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
public class TeacherManager implements AuthenticationProvider, InitializingBean {
    
    private HashMap<String, Teacher> teacherDB = new HashMap<>(); // 실제 DB가 있다면 DB에 갔다와야겠지만, 지금은 test이므로 HashMap으로 DB를 대체함
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        TeacherAuthenticationToken token = (TeacherAuthenticationToken) authentication;
        
        if (teacherDB.containsKey(token.getCredentials())) {
            
            Teacher teacher = teacherDB.get(token.getCredentials());
            
            return TeacherAuthenticationToken.builder()
                .principal(teacher)
                .details(teacher.getUsername())
                .authenticated(true)
                .build();
        }
        
        return null;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == TeacherAuthenticationToken.class;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    
        Set.of(
            new Teacher("choi", "최선생", Set.of(new SimpleGrantedAuthority("ROLE_TEACHER")))
        ).forEach(teacher ->
            teacherDB.put(teacher.getId(), teacher)
        );
    }
}
