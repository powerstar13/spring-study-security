package com.sp.fc.web.controller;

import com.sp.fc.user.domain.SpUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SessionController {
    
    private final SessionRegistry sessionRegistry;
    
    @GetMapping("/sessions")
    public String sessions(Model model) {
    
        model.addAttribute("sessionList",
            sessionRegistry.getAllPrincipals().stream()
                .map(principal ->
                    UserSession.builder()
                        .username(((SpUser) principal).getUsername())
                        .sessions(
                            sessionRegistry.getAllSessions(principal, false).stream()
                                .map(session ->
                                    SessionInfo.builder()
                                        .sessionId(session.getSessionId())
                                        .time(session.getLastRequest())
                                        .build()
                                ).collect(Collectors.toList())
                        )
                        .build()
                ).collect(Collectors.toList())
        );
        
        return "/sessionList";
    }
    
    @PostMapping("/session/expire")
    public String expireSession(@RequestParam String sessionId) {
        
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        
        if (!sessionInformation.isExpired()) {
            sessionInformation.expireNow();
        }
        
        return "redirect:/sessions";
    }
    
    @GetMapping("/session-expired")
    public String sessionExpired() {
        return "/sessionExpired";
    }
}
